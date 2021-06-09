package com.chatapp.example.flamingoapp.phase2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chatapp.example.flamingoapp.adapters.UsersAdapter;
import com.chatapp.example.flamingoapp.fragments.ChatFragment;
import com.chatapp.example.flamingoapp.models.Users;
import com.google.firebase.database.FirebaseDatabase;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityChatListBinding;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    ActivityChatListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        ChatFragment chatFragment = new ChatFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, chatFragment).commit();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatListActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

    }
}