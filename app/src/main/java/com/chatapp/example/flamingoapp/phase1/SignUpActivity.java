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

        binding.signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etUserName.getText().toString().isEmpty()) {
                    binding.etUserName.setError("Enter your username!!");
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
                    binding.etPassword.setError("Enter your password in more than 8 characters!!");
                    return;
                }


                Intent i = new Intent(SignUpActivity.this, OtpActivity.class);
                i.putExtra("email", binding.etEmail.getText().toString());
                i.putExtra("username", binding.etUserName.getText().toString());
                i.putExtra("password", binding.etPassword.getText().toString());
                startActivity(i);
            }
        });


    }
}