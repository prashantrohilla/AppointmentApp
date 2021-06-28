package com.chatapp.example.flamingoapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.adapters.MyPhotoAdapter;
import com.chatapp.example.flamingoapp.adapters.PostAdapter;
import com.chatapp.example.flamingoapp.adapters.UsersAdapter;
import com.chatapp.example.flamingoapp.models.Post;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase1.LoginActivity;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.chatapp.example.flamingoapp.phase2.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityLoginBinding;
import com.phone.DoctorAppointment.databinding.FragmentChatBinding;
import com.phone.DoctorAppointment.databinding.FragmentProfileBinding;
import com.phone.DoctorAppointment.databinding.FragmentSearchBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    MyPhotoAdapter myPhotoAdapter;
    MyPhotoAdapter mysavePhotoAdapter;
    List<Post> postList;

    List<String> mySaves;
    List<Post> postListSaves;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=  FragmentProfileBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });

        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.mySavePosts.setVisibility(View.INVISIBLE);
                binding.myPosts.setVisibility(View.VISIBLE);
            }
        });

        binding.tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.myPosts.setVisibility(View.INVISIBLE);
                binding.mySavePosts.setVisibility(View.VISIBLE);
            }
        });

        userDetails();
        getFollowers();
        savePosts();

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

    private  void myPhotos()
    {
       DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Posts");

       reference.addValueEventListener(new ValueEventListener() {  // getting list of users from database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();                                                                   // taking data snapshot from firebase
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // to avoid slow loading
                   Post post= dataSnapshot.getValue(Post.class);
                   if(post.getPublisher().equals(auth.getUid()))
                   {
                       postList.add(post);
                   }
                     Collections.reverse(postList);
                    myPhotoAdapter.notifyDataSetChanged();
                    Log.d("refernce debug"," "+postList.size());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userDetails()
    {
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
                        binding.userBio.setText(users.getUserBio());
                        binding.userLink.setText(users.getUserLink());


                        binding.myPosts.setHasFixedSize(true);
                        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                        binding.myPosts.setLayoutManager(mLayoutManager);

                        postList = new ArrayList<>();
                        myPhotoAdapter = new MyPhotoAdapter(getContext(), postList);
                        binding.myPosts.setAdapter(myPhotoAdapter);

                        myPhotos();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void savePosts()
    {
        mySaves=new ArrayList<>();
        FirebaseUser  firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference saveReference=FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        saveReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    mySaves.add(snapshot1.getKey());
                }

                binding.mySavePosts.setHasFixedSize(true);
                LinearLayoutManager saveLayoutManager = new GridLayoutManager(getContext(), 3);
                binding.mySavePosts.setLayoutManager(saveLayoutManager);

                postListSaves= new ArrayList<>();
                mysavePhotoAdapter = new MyPhotoAdapter(getContext(), postListSaves);
                binding.mySavePosts.setAdapter(mysavePhotoAdapter);
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void readSaves()
    {
        postListSaves.clear();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Post post=snapshot1.getValue(Post.class);

                    for(String id:mySaves)
                    {
                        if(post.getPostId().equals(id))
                        {
                            postListSaves.add(post);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}