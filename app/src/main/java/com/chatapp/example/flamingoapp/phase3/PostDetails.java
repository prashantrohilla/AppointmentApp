package com.chatapp.example.flamingoapp.phase3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chatapp.example.flamingoapp.models.Post;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase2.CommentActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityUserPicBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class PostDetails extends AppCompatActivity {

    ActivityUserPicBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserPicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        String postId = getIntent().getStringExtra("postId");
        String publisher = getIntent().getStringExtra("publisher");
        postDetail(postId,publisher);

        binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("postId",postId);
                intent.putExtra("publisher",publisher);
                startActivity(intent);
            }
        });

        binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( binding.likeButton.getTag().equals("like"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(postId).child(publisher).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(postId).child(publisher).removeValue();
                }
            }
        });

    }

    public void postDetail(String postId, String publisher)
    {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                binding.likeText.setText(snapshot.getChildrenCount()+" likes");
                if(snapshot.child(publisher).exists())
                {
                   binding.likeButton.setImageResource(R.drawable.liked);
                    binding.likeButton.setTag("liked");
                }
                else
                {
                    binding.likeButton.setImageResource(R.drawable.heart);
                    binding.likeButton.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        reference= FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                binding.commentText.setText("View All "+snapshot.getChildrenCount()+" Comments");            // view all comments
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Post post=snapshot.getValue(Post.class);
                Picasso.get().load(post.getPostImage()).into(binding.post);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(publisher);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               Users user=snapshot.getValue(Users.class);
                binding.publisher.setText(user.getUserName());           // view all comments
                Picasso.get().load(user.getProfilepic()).into(binding.profilepic);
                binding.etUserName.setText(user.getUserName());


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}