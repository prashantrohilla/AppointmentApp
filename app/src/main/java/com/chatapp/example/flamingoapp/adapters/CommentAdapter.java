package com.chatapp.example.flamingoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.example.flamingoapp.models.Comment;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.chatapp.example.flamingoapp.phase3.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;
    private  FirebaseAuth mUser;

    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mUser=FirebaseAuth.getInstance();
        Comment comment=mComment.get(position);
        holder.comment.setText(comment.getComment());
        getUserInfo(holder.profilepic, holder.username, comment.getPublisher());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.getPublisher().equals(mUser.getUid()))
                {
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    intent.putExtra("me", 1);
                    mContext.startActivity(intent);
                }
                else
                {

                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                    intent.putExtra("userId",comment.getPublisher() );
                    mContext.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilepic;
        TextView username,comment;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            profilepic=itemView.findViewById(R.id.commentprofilepic);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherId)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              Users user= snapshot.getValue(Users.class);
                assert user != null;
                Glide.with(mContext).load(user.getProfilepic()).into(imageView);
                username.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
