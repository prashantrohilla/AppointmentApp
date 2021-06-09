package com.chatapp.example.flamingoapp.phase3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chatapp.example.flamingoapp.phase2.ChatDetailActivity;
import com.chatapp.example.flamingoapp.phase2.ChatListActivity;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityUserProfileBinding;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        String userId = getIntent().getStringExtra("userId");
        String profilePic = getIntent().getStringExtra("profilePic");
        String userName = getIntent().getStringExtra("userName");
        String fullName = getIntent().getStringExtra("fullName");

        String userBio = getIntent().getStringExtra("userBio");
        String userLink = getIntent().getStringExtra("userLink");

        Picasso.get().load(profilePic).placeholder(R.drawable.user2).into(binding.userPic);
        binding.userName.setText(userName);
        binding.userFullName.setText(fullName);
        binding.userBio.setText(userBio);
        binding.userLink.setText(userLink);


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