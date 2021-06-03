package com.chatapp.example.flamingoapp.phase2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityChatListBinding;

public class ChatListActivity extends AppCompatActivity {

    ActivityChatListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}