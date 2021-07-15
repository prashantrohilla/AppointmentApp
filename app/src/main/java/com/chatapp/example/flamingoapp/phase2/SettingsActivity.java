package com.chatapp.example.flamingoapp.phase2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.models.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivitySettingsBinding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Uri imageUri;
    String myUri ="";
    StorageTask uploadTask;
    byte[] new_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
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
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
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

                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
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

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();

            //  image compression

            File actualImage=new File(imageUri.getPath());
            try {
                Bitmap compressedImage = new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(640)
                        .setQuality(75)
                        .compressToBitmap(actualImage);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                new_image = baos.toByteArray();
                uploadPost(new_image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            binding.showProfilePic.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this,"Something gone wrong !!",Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadPost(byte [] final_image)
    {
        if(imageUri !=null)
        {
            final StorageReference reference =
                    storage.getReference().child("profile picture")
                            .child(Objects.requireNonNull(auth.getUid()));

            uploadTask=reference.putBytes(new_image);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull @NotNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {
                    if(task.isSuccessful())
                    {
                        Uri download= (Uri) task.getResult();
                        myUri=download.toString();

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {             // getting image reference from database
                                database.getReference().child("Users").child(auth.getUid())
                                        .child("profilepic").setValue(uri.toString());

                                Toast.makeText(SettingsActivity.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(SettingsActivity.this ,"Post Failed",Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(SettingsActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        } else
        {
            Toast.makeText(SettingsActivity.this,"No Image Selected" ,Toast.LENGTH_SHORT).show();
        }

    }
}