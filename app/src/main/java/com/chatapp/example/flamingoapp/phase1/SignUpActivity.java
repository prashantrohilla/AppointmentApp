package com.chatapp.example.flamingoapp.phase1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.phone.DoctorAppointment.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.fullName.getText().toString().isEmpty()) {
                    binding.fullName.setError("Enter your Full name!!");
                    return;
                }
                if (binding.fullName.getText().toString().length()>40) {
                    binding.fullName.setError("Full name must  be in less than 40 letters !!");
                    return;
                }
                if (binding.etUserName.getText().toString().isEmpty()) {
                    binding.etUserName.setError("Enter your username!!");
                    return;
                }
                if(binding.etUserName.getText().toString().contains(" ")){
                    binding.etUserName.setError("Username must have no space!!");
                    return;
                }
                if(binding.etUserName.getText().toString().length()>40){
                    binding.etUserName.setError("Username must  be in less than 40 letters !!");
                    return;
                }

                if (binding.etEmail.getText().toString().isEmpty()) {
                    binding.etEmail.setError("Enter your email!!");
                    return;
                }

                if (binding.etPassword.getText().toString().isEmpty()) {
                    binding.etPassword.setError("Enter your password!!");
                    return;
                }
                if (binding.etPassword.getText().toString().length() <= 7) {
                    binding.etPassword.setError("Password must have 8 characters!!");
                    return;
                }


                Intent i = new Intent(SignUpActivity.this, OtpActivity.class);
                i.putExtra("email", binding.etEmail.getText().toString());
                i.putExtra("username", binding.etUserName.getText().toString().toLowerCase());
                i.putExtra("fullName", binding.fullName.getText().toString());
                i.putExtra("password", binding.etPassword.getText().toString());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });


    }
}