package com.chatapp.example.flamingo.phase1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityOtpBinding;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    ActivityOtpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        final String username = getIntent().getStringExtra("username");
        final String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");

        final EditText inputMobile = findViewById(R.id.inputMobile);
        final EditText code = findViewById(R.id.countryCode);


        binding.buttonGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.inputMobile.getText().toString().isEmpty()) {
                    binding.inputMobile.setError("Enter mobile number");
                    return;
                }
                if (binding.countryCode.getText().toString().isEmpty()) {
                    binding.countryCode.setError("Enter your country code");
                    return;
                }
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.buttonGetOTP.setVisibility(View.INVISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(    // number verification with country code
                        "+" + code.getText().toString() + inputMobile.getText().toString(), 60,
                        TimeUnit.SECONDS, OtpActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                binding.progressBar.setVisibility(View.GONE);
                                binding.buttonGetOTP.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.buttonGetOTP.setVisibility(View.VISIBLE);
                                Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.buttonGetOTP.setVisibility(View.VISIBLE);

                                Intent i = new Intent(OtpActivity.this, OtpNextActivity.class);
                                i.putExtra("number", inputMobile.getText().toString());  // taking number
                                i.putExtra("verificationId", verificationId);
                                i.putExtra("username", username);
                                i.putExtra("email", email);
                                i.putExtra("password", password);
                                i.putExtra("countryCode", code.getText().toString());
                                startActivity(i);
                            }
                        });


            }
        });
    }
}