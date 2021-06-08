package com.chatapp.example.flamingoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.example.flamingoapp.fragments.ProfileFragment;
import com.chatapp.example.flamingoapp.fragments.SearchFragment;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase3.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder>{

    Context mContext;
    static ArrayList<Users> mUsers;
    FirebaseUser firebaseUser;


    public FollowAdapter(Context mContext, ArrayList<Users> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int viewType) {             // setting sample follow layout here
       View view= LayoutInflater.from(mContext).inflate(R.layout.sample_follow_user, viewGroup,false);
        return new FollowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
                                                       // putting all data in views
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Users user=mUsers.get(position);

        holder.followButton.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUserName());
        holder.fullName.setText(user.getFullName());
        Picasso.get().load(user.getProfilepic())
                .placeholder(R.drawable.user2)
                .into(holder.profilePic);

        isFollowing(user.getUserId(), holder.followButton);// passing id and button to method

        if(user.getUserId().equals(firebaseUser.getUid())) // if our profile then hide follow button
        {
            holder.followButton.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   SharedPreferences.Editor editor= mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            //    editor.putString("profileId",user.getUserId());
            //    editor.apply();

        //  ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLyout,new ProfileFragment()).commit();

                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("userId", user.getUserId());
                intent.putExtra("profilePic", user.getProfilepic());
                intent.putExtra("userName", user.getUserName());
                intent.putExtra("fullName", user.getFullName());
                intent.putExtra("userBio", user.getUserBio());
                intent.putExtra("userLink", user.getUserLink());
                mContext.startActivity(intent);

            }
        });


        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.followButton.getText().toString().equals("follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(user.getUserId())
                            .setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUserId()).child("followers").child(firebaseUser.getUid())
                            .setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("following").child(user.getUserId())
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUserId()).child("followers").child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {   // setting all textviews and buttons of follow layout

        public TextView username, fullName, followButton;
        public CircleImageView profilePic;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.userName);
            fullName=itemView.findViewById(R.id.fullName);
            profilePic=itemView.findViewById(R.id.profile_pic);
            followButton=itemView.findViewById(R.id.followButton);
        }
    }

    private void isFollowing(final String userId, final TextView button)
    { DatabaseReference reference= FirebaseDatabase.getInstance()
                .getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 if(snapshot.child(userId).exists())
                 {
                     button.setText("following");
                 }
                 else
                 {
                     button.setText("follow");
                 }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}





















