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

                String username=binding.signupUserName.getText().toString();
                String fullname=binding.signupFullName.getText().toString();
                String email=binding.signupEmail.getText().toString();
                String password=binding.signupPassword.getText().toString();



                if (username.isEmpty() ) {
                    binding.signupUserName.setError("Enter a user name!!");
                    return;
                }
                if (username.length()<6 || username.length()>20) {
                    binding.signupUserName.setError("User name must  be in 6-20 letters !!");
                    return;
                }

                if(username.contains(" "))
                {
                    binding.signupUserName.setError("User name should not contain space !!");
                    return;
                }

                if (binding.signupFullName.getText().toString().isEmpty() ) {
                    binding.signupFullName.setError("Enter your Full name !!");
                    return;
                }

                if (email.isEmpty()) {
                    binding.signupEmail.setError("Enter your email !!");
                    return;
                }

                boolean mail=validateEmailAddress(email);
                if(!mail)
                {
                    binding.signupEmail.setError("Enter valid email address !!");
                    return;
                }

                if (password.isEmpty()) {
                    binding.signupPassword.setError("Enter your password !!");
                    return;
                }

                boolean passwrd=validatePassword(password);

                if(!passwrd)
                {
                    binding.signupPassword.setError("Enter a strong password with special character and symbols!!");
                    return;
                }

                Intent i = new Intent(SignUpActivity.this, OtpActivity.class);
                i.putExtra("email", email);
                i.putExtra("username", username.toLowerCase());
                i.putExtra("fullName",fullname);
                i.putExtra("password", password);
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