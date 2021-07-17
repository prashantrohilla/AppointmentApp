package com.chatapp.example.flamingoapp.phase3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityStoryImageBinding;
import com.squareup.picasso.Picasso;

public class StoryImageActivity extends AppCompatActivity {

    ActivityStoryImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityStoryImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        String story= getIntent().getStringExtra("story");
        Picasso.get().load(story).placeholder(R.drawable.user).into(binding.storyPic);
    }
}