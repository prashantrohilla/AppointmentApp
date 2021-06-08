package com.chatapp.example.flamingoapp.phase1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.phone.DoctorAppointment.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    ActivityWelcomeBinding binding;  // dont have to use findview id of views
    FirebaseDatabase database;
    ProgressDialog progressDialog; // loading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(WelcomeActivity.this);
        progressDialog.setTitle("Creating Account");   // loading while creating account.
        progressDialog.setMessage("We're creating your account..");

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                String username = getIntent().getStringExtra("username");
                String email = getIntent().getStringExtra("email");
                String password = getIntent().getStringExtra("password");
                String mobile = getIntent().getStringExtra("number");
                String fullName = getIntent().getStringExtra("fullName");

                Users user = new Users(username, email, password, mobile, fullName);         // taking username, email, password in database.
                String id = getIntent().getStringExtra("uuid");

                database.getReference().child("Users").child(id).setValue(user);   // it will create a user node in firebase containing userId / mail , password, username
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                progressDialog.hide();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(WelcomeActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
}