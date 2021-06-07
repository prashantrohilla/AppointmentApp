package com.chatapp.example.flamingoapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chatapp.example.flamingoapp.adapters.FollowAdapter;
import com.chatapp.example.flamingoapp.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.FragmentSearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FollowAdapter followAdapter;
    private List<Users> mUsers;
    EditText searchUser;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view=inflater.inflate(R.layout.fragment_search, container, false);

         RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

         searchUser= view.findViewById(R.id.searchUser);

         mUsers=new ArrayList<>();
         followAdapter=new FollowAdapter(getContext(),mUsers);
         recyclerView.setAdapter(followAdapter);

         readUsers();
         searchUser.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 searchUsers(charSequence.toString().toLowerCase());
             }

             @Override
             public void afterTextChanged(Editable editable) {

             }
         });

         return view;
    }

    private void searchUsers(String s)
    {
        Query query= FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("userName").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    Users user=snapshot1.getValue(Users.class);
                    mUsers.add(user);
                }

                followAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void readUsers()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(searchUser.getText().toString().equals(""))
                {
                    mUsers.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren())
                    {
                        Users user=snapshot1.getValue(Users.class);
                        mUsers.add(user);
                    }
                    followAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}