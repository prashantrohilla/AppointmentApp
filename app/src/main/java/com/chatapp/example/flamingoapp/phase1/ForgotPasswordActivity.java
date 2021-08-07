package com.chatapp.example.flamingoapp.phase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityForgotPasswordBinding;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        binding.backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=binding.forgetemail.getText().toString();
                boolean mail=validateEmailAddress(email);

                if (email.isEmpty()) {
                    binding.forgetemail.setError("Enter your email !!");
                    return;
                }

                if(!mail)
                {
                    binding.forgetemail.setError("Enter valid email address !!");
                    return;
                }

                forgetPassword(email);
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

    private void forgetPassword(String email) {

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                          Toast.makeText(ForgotPasswordActivity.this,"Check your Email.",Toast.LENGTH_SHORT).show();
                          Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                          startActivity(intent);
                          finish();
                      }
                      else
                      {
                          Toast.makeText(ForgotPasswordActivity.this,"Error : "+task.getException(),Toast.LENGTH_SHORT).show();
                      }
                    }
                });
    }

}