package com.chatapp.example.flamingoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityOptionsBinding;

public class OptionsActivity extends AppCompatActivity {
    ActivityOptionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}