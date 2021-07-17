package com.chatapp.example.flamingoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.example.flamingoapp.models.Notification;
import com.chatapp.example.flamingoapp.models.Post;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase2.ChatDetailActivity;
import com.chatapp.example.flamingoapp.phase3.PostDetails;
import com.chatapp.example.flamingoapp.phase3.UserProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Notification notification=mNotification.get(position);
        holder.comment.setText(notification.getComment());
        getUserInfo(holder.profilePic, holder.userName, notification.getUserId());

        if(notification.isPost())
        {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostId());
        }
        else
        {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isPost())
                {
                    Intent intent = new Intent(mContext, PostDetails.class);
                    intent.putExtra("postId", notification.getPostId());
                    intent.putExtra("publisher", notification.getUserId());
                    mContext.startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                    intent.putExtra("userId", notification.getUserId());
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePic;
        ImageView postImage;
        TextView userName, comment;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            profilePic=itemView.findViewById(R.id.profilePic);
            postImage=itemView.findViewById(R.id.postImage);
            userName=itemView.findViewById(R.id.userName);
            comment=itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(CircleImageView imageView, TextView username, String publisherId)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance()
                .getReference("Users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Users user= snapshot.getValue(Users.class);
                Glide.with(mContext).load(user.getProfilepic()).into(imageView);
                username.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(ImageView imageView, final String postId) {
        Log.d("1 Post id"," "+postId);
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostImage()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}
