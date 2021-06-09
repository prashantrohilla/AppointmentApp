package com.chatapp.example.flamingoapp.phase1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityOtpNextBinding;


public class OtpNextActivity extends AppCompatActivity {

    ActivityOtpNextBinding binding;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    FirebaseAuth auth;
    FirebaseDatabase database;

    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpNextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String number = getIntent().getStringExtra("number");
        String countryCode = getIntent().getStringExtra("countryCode");
        String mobile = "+" + countryCode + number;
        binding.textMobile.setText(mobile);

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();


        verificationId = getIntent().getStringExtra("verificationId");  // taking code from intent
        binding.buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputCode1.getText().toString().trim().isEmpty()
                        || inputCode2.getText().toString().trim().isEmpty()
                        || inputCode3.getText().toString().trim().isEmpty()
                        || inputCode4.getText().toString().trim().isEmpty()
                        || inputCode5.getText().toString().trim().isEmpty()
                        || inputCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(OtpNextActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code = inputCode1.getText().toString() + inputCode2.getText().toString() + inputCode3.getText().toString()
                        + inputCode4.getText().toString() + inputCode5.getText().toString() + inputCode6.getText().toString();
                if (verificationId != null) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.buttonVerify.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId, code);

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String number = getIntent().getStringExtra("number");
                                        String countryCode = getIntent().getStringExtra("countryCode");
                                        String username = getIntent().getStringExtra("username");
                                        String fullName = getIntent().getStringExtra("fullName");

                                        String mobile = "+" + countryCode + number;

                                        String email = getIntent().getStringExtra("email");
                                        String password = getIntent().getStringExtra("password");
                                        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

                                        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(authCredential);
                                        Toast.makeText(OtpNextActivity.this, "OTP Verified Successfully", Toast.LENGTH_SHORT).show();
                                        finishAffinity();

                                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Log.d("theotp", FirebaseAuth.getInstance().getUid());
                                        intent.putExtra("number", mobile);              // sending number
                                        intent.putExtra("uuid", FirebaseAuth.getInstance().getUid());
                                        intent.putExtra("username", username);
                                        intent.putExtra("fullName", fullName);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(OtpNextActivity.this, "Invalid verification code entered", Toast.LENGTH_SHORT).show();
                                        finishAffinity();
                                        Intent i = new Intent(OtpNextActivity.this, SignUpActivity.class);
                                        startActivity(i);
                                    }
                                }
                            });
                }
            }

        });

        binding.textResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                Intent i = new Intent(OtpNextActivity.this, OtpActivity.class);
                startActivity(i);
            }
        });
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}