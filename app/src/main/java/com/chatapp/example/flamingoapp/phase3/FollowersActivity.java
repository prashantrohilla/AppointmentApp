
package com.chatapp.example.flamingoapp.phase3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.chatapp.example.flamingoapp.adapters.CommentAdapter;
import com.chatapp.example.flamingoapp.adapters.FollowAdapter;
import com.chatapp.example.flamingoapp.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityFollowersBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    ActivityFollowersBinding binding;
    String id;
    String title;;

    List<String> listId;
    List<Users> userList;
    FollowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        id = getIntent().getStringExtra("Id");
      //  Log.d("intent id", ""+id);
      //  Log.d("followers", ""+followers);
        title = getIntent().getStringExtra("Title");



        binding.followersRecyclerView.setHasFixedSize(true);
        binding.followersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList= new ArrayList<>();
        adapter=new FollowAdapter(this, (ArrayList<Users>) userList);
        binding.followersRecyclerView.setAdapter(adapter);


        listId = new ArrayList<>();

        switch (title) {
            case "likes":
              //  getLikes();
                break;
            case "following":
              //  getFollowing();
                break;
            case "followers":
                getFollowers();
                break;
        }

    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listId.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    listId.add(snapshot.getKey());
                    //Log.d("list id ",""+snapshot.getKey());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUsers() {

        FirebaseDatabase database;
        database=FirebaseDatabase.getInstance();
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {  // getting list of users from database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();                                                                    // taking data snapshot from firebase
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    // to avoid slow loading
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                   // users.setUserId(dataSnapshot.getKey());   // getting user name on base of user id from firebase

                       Log.d("user id",""+users.getUserId());
                    for (String id : listId){
                        if (users.getUserId().equals(id)){
                            userList.add(users);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void getLikes() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
//                .child(id);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                    listId.add(snapshot1.getKey());
//                }
//                showUser();
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }

//    private void getFollowing() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
//                .child(id).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//                   // listId.add(snapshot1.getKey());
//                }
//                showUser();
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }

}