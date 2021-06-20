package com.chatapp.example.flamingoapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.models.Post;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase1.LoginActivity;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.chatapp.example.flamingoapp.phase2.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityLoginBinding;
import com.phone.DoctorAppointment.databinding.FragmentChatBinding;
import com.phone.DoctorAppointment.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

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
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });


        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        assert users != null;
                        Picasso.get().load(users.getProfilepic())
                                .placeholder(R.drawable.user2)
                                .into(binding.profilePic);
                        binding.userName.setText(users.getFullName());
                        binding.uName.setText(users.getUserName());

                        if(users.getUserBio()!=null)
                        {
                            binding.userBio.setText(users.getUserBio());
                            binding.userBio.setVisibility(View.VISIBLE);
                        }

                        if(users.getUserLink()!=null)
                        {
                            binding.userLink.setText(users.getUserLink());
                            binding.userLink.setVisibility(View.VISIBLE);
                        }

                        getFollowers();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }

    private void getFollowers()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(auth.getUid()).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                binding.followersText.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(auth.getUid()).child("following");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                binding.followingText.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference reference3=FirebaseDatabase.getInstance().getReference()
                .child("Posts");

        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              int i=0;
              for(DataSnapshot snapshot1:snapshot.getChildren())
              {
                  Post post=snapshot1.getValue(Post.class);
                  if(post.getPublisher().equals(auth.getUid()))
                  {
                      Log.d("refernce debug"," "+post.getPublisher());
                      i++;
                  }
              }
                binding.postText.setText(""+i);


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}