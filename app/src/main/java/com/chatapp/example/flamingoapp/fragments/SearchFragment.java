package com.chatapp.example.flamingoapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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


public class SearchFragment extends Fragment{

    ArrayList<Users> mUsers =new ArrayList<>();
    FragmentSearchBinding binding;
    FirebaseDatabase database;
    ArrayList<Users> mUser=new ArrayList<>();


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=  FragmentSearchBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();// not giving this have given error at 2 hour

        Search(null);

        binding.searchUser.addTextChangedListener(new TextWatcher() {   // to show typing status
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Search(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
        return binding.getRoot();
    }

    public void Search(String newText) {
        if (newText != null)
        {
            mUser.clear();
            for (Users user : mUsers)
            {
                if (user.getUserName().contains(newText))
                {
                    mUser.add(user);
                }
            }

            FollowAdapter adapter=new FollowAdapter(getContext(), mUser);                         // setting adapter
            binding.searchRecyclerView.setAdapter(adapter);// setting adapter on recycler
            binding.searchRecyclerView.setHasFixedSize(true);

            binding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    RecyclerView.VERTICAL,false));
            adapter.notifyDataSetChanged();

        }
        else
        {
            database.getReference().child("Users").addValueEventListener(new ValueEventListener() {  // getting list of users from database
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUser.clear();                                                                    // taking data snapshot from firebase
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        // to avoid slow loading
                        Users users = dataSnapshot.getValue(Users.class);
                        assert users != null;
                        users.setUserId(dataSnapshot.getKey());                                     // getting user name on base of user id from firebase
                        if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))  // not showing current login user
                            mUsers.add(users);
                    }

                    FollowAdapter adapter=new FollowAdapter(getContext(), mUsers);                         // setting adapter
                    binding.searchRecyclerView.setAdapter(adapter);// setting adapter on recycler
                    binding.searchRecyclerView.setHasFixedSize(true);

                    binding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                            RecyclerView.VERTICAL,false));
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

}