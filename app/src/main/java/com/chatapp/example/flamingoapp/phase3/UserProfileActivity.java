package com.chatapp.example.flamingoapp.phase3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase2.ChatDetailActivity;
import com.chatapp.example.flamingoapp.phase2.ChatListActivity;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityUserProfileBinding;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    String profilePic;
    String userName ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        String userId = getIntent().getStringExtra("userId");

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(userId);

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Users user= snapshot.getValue(Users.class);
                            assert user != null;
                            Picasso.get().load(user.getProfilepic()).placeholder(R.drawable.user2).into(binding.userPic);
                            binding.userName.setText(user.getUserName());
                            binding.userFullName.setText(user.getFullName());
                            binding.userBio.setText(user.getUserBio());
                            binding.userLink.setText(user.getUserLink());
                            profilePic=user.getProfilepic();
                            userName=user.getUserName();

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });


        binding.chatSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserProfileActivity.this, ChatListActivity.class);
                startActivity(i);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserProfileActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });


        binding.message.setOnClickListener(new View.OnClickListener() {  // sending user data to chat detail activity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, ChatDetailActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("profilePic", profilePic);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });

    }
}