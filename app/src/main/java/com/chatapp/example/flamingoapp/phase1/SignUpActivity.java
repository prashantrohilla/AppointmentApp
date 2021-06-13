package com.chatapp.example.flamingoapp.phase1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.phone.DoctorAppointment.databinding.ActivitySignUpBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                String email = binding.etEmail.getText().toString().trim();

                if(email.isEmpty())
                {
                    binding.etEmail.setError("Enter your email!!");
                    return;
                }

                boolean mail=validateEmailAddress(email);
                if(!mail)
                {
                    binding.etEmail.setError("Enter valid email address !!");
                    return;
                }

                String password=binding.etPassword.getText().toString().trim();

                boolean passwrd=validatePassword(password);

                if(!passwrd)
                {
                    binding.etPassword.setError("Enter a strong password !!");
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

    private boolean validateEmailAddress(String emailAddress){
        String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    private boolean validatePassword(String password){
        String  expression="^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        CharSequence inputStr = password;
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }
}