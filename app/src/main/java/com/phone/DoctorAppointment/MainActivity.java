package com.phone.DoctorAppointment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.phone.DoctorAppointment.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        Toast.makeText(this, "Click Sign In", Toast.LENGTH_SHORT).show();
        
          binding.signIn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent i=new Intent(MainActivity.this,AppointmentActivity.class);
                  startActivity(i);
              }
          });

    }
}