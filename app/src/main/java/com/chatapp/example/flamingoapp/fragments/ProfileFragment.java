package com.chatapp.example.flamingoapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase1.LoginActivity;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.chatapp.example.flamingoapp.phase2.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityLoginBinding;
import com.phone.DoctorAppointment.databinding.FragmentChatBinding;
import com.phone.DoctorAppointment.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=  FragmentProfileBinding.inflate(inflater, container, false);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();// user logout
                Toast.makeText(getContext(), "You have been logged out.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        assert users != null;
                        Picasso.get().load(users.getProfilepic())
                                .placeholder(R.drawable.user)
                                .into(binding.profilePic);
                        binding.userName.setText(users.getUserName());
                       // binding.userBio.setText(users.getUserBio());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }
}