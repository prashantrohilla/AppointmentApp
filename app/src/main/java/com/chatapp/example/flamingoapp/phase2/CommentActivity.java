package com.chatapp.example.flamingoapp.phase2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chatapp.example.flamingoapp.adapters.CommentAdapter;
import com.chatapp.example.flamingoapp.models.Comment;
import com.chatapp.example.flamingoapp.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityCommentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    @NonNull ActivityCommentBinding binding;
    String postId;
    String publisherId;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    List<Comment> commentList;
    CommentAdapter commentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        recyclerView=findViewById(R.id.commentRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        commentList=new ArrayList<>();
        commentAdapter= new CommentAdapter(this,commentList);
        recyclerView.setAdapter(commentAdapter);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent= getIntent();
        postId=intent.getStringExtra("postId");
        publisherId=intent.getStringExtra("publisher");

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.addComment.getText().toString().equals(""))
                {
                    Toast.makeText(CommentActivity.this, "You can't send empty comment !!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addComment();
                }
            }
        });

        getImage();
        readComments();
    }

    private void addComment()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postId);

        HashMap<String , Object> hashMap= new HashMap<>();         // storing comments
        hashMap.put("comment",binding.addComment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addNotifications();
        binding.addComment.setText("");
    }

    private void getImage()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Users user=snapshot.getValue(Users.class);
                Glide.with(getApplicationContext()).load(user.getProfilepic()).into(binding.profilePic);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void readComments()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               commentList.clear();
               for(DataSnapshot snapshot1:snapshot.getChildren())
               {
                   Comment comment=snapshot1.getValue(Comment.class);
                   commentList.add(comment);
               }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addNotifications()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("Notifications").child(publisherId);

        
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("comment","commented ");
        hashMap.put("postId",postId);
        hashMap.put("isPost",true);

        reference.push().setValue(hashMap);
    }
}