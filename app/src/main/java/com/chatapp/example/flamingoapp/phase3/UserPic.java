package com.chatapp.example.flamingoapp.phase3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityUserPicBinding;
import com.squareup.picasso.Picasso;

public class UserPic extends AppCompatActivity {

    ActivityUserPicBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserPicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        String post = getIntent().getStringExtra("post");
        Picasso.get().load(post).into(binding.post);
    }
}