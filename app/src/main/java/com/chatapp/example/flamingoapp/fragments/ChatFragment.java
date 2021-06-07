package com.chatapp.example.flamingoapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.example.flamingoapp.adapters.UsersAdapter;
import com.chatapp.example.flamingoapp.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.databinding.FragmentChatBinding;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    FragmentChatBinding binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment        //binding work in fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();                                               // not giving this have given error at 2 hour
        final UsersAdapter adapter = new UsersAdapter(list, getContext());                         // setting adapter
        binding.chatRecyclerView.setAdapter(adapter);                                            // setting adapter on recycler

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        binding.chatRecyclerView.showShimmerAdapter();

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.searchText.getText().toString().isEmpty()) {
                    binding.searchText.setError("Enter a username !!");
                    return;

                }
            }
        });

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {  // getting list of users from database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();                                                                    // taking data snapshot from firebase
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // to avoid slow loading
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());                                     // getting user name on base of user id from firebase
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))  // not showing current login user
                        list.add(users);
                }
                binding.chatRecyclerView.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();

    }
}