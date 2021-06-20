package com.chatapp.example.flamingoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.example.flamingoapp.models.Post;
import com.chatapp.example.flamingoapp.phase3.UserPic;
import com.chatapp.example.flamingoapp.phase3.UserProfileActivity;
import com.phone.DoctorAppointment.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder>{

    private Context context;
    private List<Post> mPosts;

    public MyPhotoAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @NotNull
    @Override
    public MyPhotoAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_photo_item, parent,false);
        return new MyPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        final Post post = mPosts.get(position);
        Log.d("refernce mposts debug"," "+mPosts.get(position));

        Glide.with(context).load(post.getPostImage())
                .into(holder.postImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Post post=mPosts.get(position);
                Intent intent = new Intent(context, UserPic.class);
                intent.putExtra("post", post.getPostImage());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView postImage;

    public ViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        postImage=itemView.findViewById(R.id.myPost);
    }
 }
}
