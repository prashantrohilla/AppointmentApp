package com.chatapp.example.flamingoapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.adapters.FollowAdapter;
import com.chatapp.example.flamingoapp.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.Constants;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.FragmentSearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FollowAdapter followAdapter;
    ArrayList<Users> mUsers =new ArrayList<>();
    FragmentSearchBinding binding;
    FirebaseDatabase database;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=  FragmentSearchBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();                                               // not giving this have given error at 2 hour
        FollowAdapter adapter=new FollowAdapter(getContext(), mUsers);                         // setting adapter
        binding.searchRecyclerView.setAdapter(adapter);// setting adapter on recycler

        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        binding.searchRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {  // getting list of users from database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();                                                                    // taking data snapshot from firebase
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    // to avoid slow loading
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());                                     // getting user name on base of user id from firebase
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))  // not showing current login user
                        mUsers.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.searchUser.getText().toString();
                FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(userName).addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.i(Constants.TAG, "dataSnapshot value = " + dataSnapshot.getValue());

                                if (dataSnapshot.exists()) {

                                    // User Exists
                                    // Do your stuff here if user already exists
                                    Toast.makeText(getContext(), "Username already exists. Please try other username.", Toast.LENGTH_SHORT).show();

                                } else {

                                    // User Not Yet Exists
                                    // Do your stuff here if user not yet exists
                                }
                            }
                            @Override
                            public void onCancelled (DatabaseError databaseError){

                            }
                        });
            }
        });

        return binding.getRoot();
    }

}