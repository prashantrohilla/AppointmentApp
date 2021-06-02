package com.chatapp.example.flamingo.phase2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.chatapp.example.flamingo.fragments.AddPostFragment;
import com.chatapp.example.flamingo.fragments.HomeFragment;
import com.chatapp.example.flamingo.fragments.NotificationFragment;
import com.chatapp.example.flamingo.fragments.ProfileFragment;
import com.chatapp.example.flamingo.fragments.SearchFragment;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,new HomeFragment());
        transaction.commit();

        binding.readableBottomBar.setOnItemSelectListener( new ReadableBottomBar.ItemSelectListener(){
            @Override
            public void onItemSelected(int i){

                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                
                switch(i)
                {
                    case 0: transaction.replace(R.id.container,new HomeFragment());
                    break;

                    case 1: transaction.replace(R.id.container,new SearchFragment());
                    break;

                    case 2: transaction.replace(R.id.container,new AddPostFragment());
                    break;

                    case 3: transaction.replace(R.id.container,new NotificationFragment());
                    break;

                    case 4: transaction.replace(R.id.container,new ProfileFragment());
                    break;
                }
                transaction.commit();
            }
        });
    }
}