package com.chatapp.example.flamingoapp.phase2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.models.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        binding.addProfilePic.setOnClickListener(new View.OnClickListener() {  // taking image from addimage
            @Override
            public void onClick(View view) {              // clicking image button
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");  //*/*
                startActivityForResult(intent, 33);
            }
        });


        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.editName.getText().toString();
                String fullName = binding.fullName.getText().toString();
                String bio = binding.editBio.getText().toString();
                String link = binding.editLink.getText().toString();

                HashMap<String, Object> obj = new HashMap<>();
                obj.put("userName", name);
                obj.put("fullName", fullName);
                obj.put("userBio", bio);
                obj.put("userLink", link);

                database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .updateChildren(obj);
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
                                .into(binding.showProfilePic);
                        binding.editName.setText(users.getUserName());
                        binding.editBio.setText(users.getUserBio());
                        binding.editLink.setText(users.getUserLink());
                        binding.fullName.setText(users.getFullName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null)               // image selected from gallery
        {
            Uri sFile = data.getData();
            binding.showProfilePic.setImageURI(sFile);         // showing image to profile image  in settings activity

            // now storing this image in firebase
            final StorageReference reference = storage.getReference().child("profile picture")
                    .child(Objects.requireNonNull(auth.getUid()));
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // now showing image in chat user list
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {             // getting image reference from database
                            database.getReference().child("Users").child(auth.getUid())
                                    .child("profilepic").setValue(uri.toString());

                            Toast.makeText(SettingsActivity.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }
    }
}