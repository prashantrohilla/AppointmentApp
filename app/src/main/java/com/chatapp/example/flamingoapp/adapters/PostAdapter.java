package com.chatapp.example.flamingoapp.adapters;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.example.flamingoapp.fragments.ProfileFragment;
import com.chatapp.example.flamingoapp.fragments.SearchFragment;
import com.chatapp.example.flamingoapp.models.Post;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    Context mContext;
    List<Post> mPosts;

    FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @NotNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int viewType) {             // setting sample follow layout here
        View view= LayoutInflater.from(mContext).inflate(R.layout.sample_post, viewGroup,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // putting all data in views

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final Post post=mPosts.get(position);
        Picasso.get().load(post.getPostImage())
                .into(holder.post);

        if(post.getDescription().equals(""))
        {
            holder.description.setVisibility(View.GONE);
        }
        else
        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.profilePic, holder.userName,holder.publisher,post.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {   // setting all textviews and buttons of follow layout

     CircleImageView profilePic;
     ImageView post,menu,like,comment,tag;
     TextView userName, likeText,publisher, commentText, description, viewAllComments;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            profilePic=itemView.findViewById(R.id.profile_pic);

            userName=itemView.findViewById(R.id.userName);
            publisher=itemView.findViewById(R.id.publisher);
            likeText=itemView.findViewById(R.id.likeText);
            commentText=itemView.findViewById(R.id.commentText);
            description=itemView.findViewById(R.id.description);
            viewAllComments=itemView.findViewById(R.id.seeComments);

            post=itemView.findViewById(R.id.post);
            menu=itemView.findViewById(R.id.menu);
            like=itemView.findViewById(R.id.likeButton);
            comment=itemView.findViewById(R.id.commentButton);
            tag=itemView.findViewById(R.id.tag);

        }
    }

    public void publisherInfo(final ImageView profilePic, final TextView userName,
                              final TextView publisher, final String userId)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Users user=snapshot.getValue(Users.class);
                assert user != null;
                Picasso.get().load(user.getProfilepic())
                        .placeholder(R.drawable.user)
                        .into(profilePic);
                userName.setText(user.getUserName());
                publisher.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

}





















