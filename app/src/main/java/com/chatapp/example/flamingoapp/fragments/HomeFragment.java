    package com.chatapp.example.flamingoapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.adapters.PostAdapter;
import com.chatapp.example.flamingoapp.adapters.StatusAdapter;
import com.chatapp.example.flamingoapp.models.Post;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase3.StoryImageActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.phone.DoctorAppointment.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment{

    ArrayList<Post> postList =new ArrayList<>();
    ArrayList<String> followingList =new ArrayList<>();
    RecyclerView recyclerView;
    PostAdapter postAdapter;

    ArrayList<Users> list =new ArrayList<>();
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FragmentHomeBinding binding;
    RecyclerView storyRecyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=  FragmentHomeBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        auth= FirebaseAuth.getInstance();


        binding.postRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        binding.postRecyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        binding.postRecyclerView.setAdapter(postAdapter);

        final StatusAdapter adapter=new StatusAdapter(list, getContext());
        binding.storyRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        binding.storyRecyclerView.setLayoutManager(layoutManager);

        binding.addStoryButton.setOnClickListener(new View.OnClickListener() {  // taking image from addimage
            @Override
            public void onClick(View view) {              // clicking status image button
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");  //*/*
                startActivityForResult(intent, 34);
            }
        });

        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users=snapshot.getValue(Users.class);
                        assert users != null;
                        Picasso.get().load(users.getProfilepic()).into(binding.myProfilePic);
                        binding.myUsername.setText("My Story");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.myProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users=snapshot.getValue(Users.class);
                                assert users != null;
                                if(users.getStatusImage()!=null)
                                {
                                    Intent intent= new Intent(getContext(), StoryImageActivity.class);
                                    intent.putExtra("story", users.getStatusImage());
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Story is Empty",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



            }
        });


        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {  // getting list of users from database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                list.clear();                                                                    // taking data snapshot from firebase
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    // to avoid slow loading
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());                                     // getting user name on base of user id from firebase
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid()) && users.getStatusImage()!=null)  // not showing current login user
                        list.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       checkFollowing();

        return binding.getRoot();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            if(data.getData()!=null)               // image selected from gallery
            {
                Uri sFile = data.getData();

                // now storing this image in firebase
                final StorageReference reference=storage.getReference().child("Status Image")
                        .child(FirebaseAuth.getInstance().getUid());
                reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // now showing image in chat user list
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {                 // getting image reference from database
                                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                        .child("statusImage").setValue(uri.toString());
                            }
                        });

                    }
                });
            }
        }

    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                postAdapter.notifyDataSetChanged();
                readPost();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : followingList){
                        assert post != null;
                        if (post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}