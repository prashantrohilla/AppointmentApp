package com.chatapp.example.flamingoapp.phase2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.chatapp.example.flamingoapp.fragments.AddPostFragment;
import com.chatapp.example.flamingoapp.fragments.HomeFragment;
import com.chatapp.example.flamingoapp.fragments.NotificationFragment;
import com.chatapp.example.flamingoapp.fragments.ProfileFragment;
import com.chatapp.example.flamingoapp.fragments.SearchFragment;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityHomeBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        bottomNavigation=findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.search));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.add));
        bottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.notification));
        bottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.profile));

        bottomNavigation.show(1,true);
        replace(new HomeFragment());
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch(model.getId())
                {
                    case 1: replace(new HomeFragment()); break;
                    case 2: replace(new SearchFragment()); break;
                    case 3: replace(new AddPostFragment()); break;
                    case 4: replace(new NotificationFragment()); break;
                    case 5: replace(new ProfileFragment()); break;
                }
                return null;
            }
        });
    }

    private void replace(Fragment fragment)
    {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLyout,fragment);
        transaction.commit();
    }
}