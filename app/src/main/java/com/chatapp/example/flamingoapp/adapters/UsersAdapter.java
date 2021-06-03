package com.chatapp.example.flamingoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase2.ChatDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// using for chat fragment
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    ArrayList<Users>  list;                                                                                           // list of users
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user, parent,false);     // attaching sample users to main activity
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {     // showing all users list
        final Users users = list.get(position);
        Picasso .get().load(users.getProfilepic()).placeholder(R.drawable.user).into(holder.image);      // online image taken from firebase profile pic not upload we will load drawable into image
        holder.userName.setText(users.getUserName());                                                    // getting user name

                                                                           // last message
        FirebaseDatabase .getInstance().getReference().child("chats")
        .child(FirebaseAuth.getInstance().getUid()+users.getUserId())
        .orderByChild("timestamp").limitToLast(1)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren())
                {
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        try {
                            holder.lastMessage.setText(decrypt(snapshot1.child("message").getValue(String.class)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {  // sending user data to chat detail activity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId",users.getUserId());
                intent.putExtra("profilePic",users.getProfilepic());
                intent.putExtra("userName",users.getUserName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();                       // if this not given we will not get chats on screen
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;           // for chat fragment
        TextView userName, lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image= itemView.findViewById(R.id.profile_image);
            userName= itemView.findViewById(R.id.userNameList);
            lastMessage= itemView.findViewById(R.id.lastMessage);
        }
    }

    public String decrypt(String data) throws Exception
    {   String AES="AES";
        String password="helloworld";
        SecretKeySpec key=generateKey(password);
        Cipher c=Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue= Base64.decode(data,Base64.DEFAULT);
        byte [] decValue = c.doFinal(decodedValue);
        String decryptedValue= new String(decValue);
        return  decryptedValue;

    }

    private SecretKeySpec generateKey(String password) throws  Exception
    {
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0,bytes.length );
        byte []key=digest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key, "AES");
        return  secretKeySpec;
    }

}
