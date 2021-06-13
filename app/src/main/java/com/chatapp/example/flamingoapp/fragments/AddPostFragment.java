package com.chatapp.example.flamingoapp.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.chatapp.example.flamingoapp.phase2.SettingsActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.phone.DoctorAppointment.databinding.FragmentAddPostBinding;
import com.phone.DoctorAppointment.databinding.FragmentHomeBinding;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;


public class AddPostFragment extends Fragment {


    public AddPostFragment() {
        // Required empty public constructor
    }

    FragmentAddPostBinding binding;
    Uri imageUri;
    String myUri ="";
    StorageTask uploadTask;
    StorageReference storageReference;            // firebase
    byte[] new_image;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();

            //  image compression

            File actualImage=new File(imageUri.getPath());
            try {
                Bitmap compressedImage = new Compressor(getContext())
                        .setMaxWidth(640)
                        .setMaxHeight(640)
                        .setQuality(75)
                        .compressToBitmap(actualImage);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                new_image = baos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }


            binding.postImage.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(getContext(),"Something gone wrong !!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), HomeActivity.class));
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater, container, false);

        storageReference= FirebaseStorage.getInstance().getReference("posts");

        binding.cancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HomeActivity.class));
            }
        });

        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost(new_image);
            }
        });

        CropImage.activity().setAspectRatio(1,1)
                .start(Objects.requireNonNull(getContext()),this);
        return binding.getRoot();
    }

    public String getFileExtension(Uri uri)
    {
        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    public void uploadPost(byte [] final_image)
    {
        ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Posting");
        progressDialog.show();

        if(imageUri !=null)
        {
            StorageReference file=storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask= file.putBytes(final_image);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull @NotNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {
                    if(task.isSuccessful())
                    {
                        Uri download= (Uri) task.getResult();
                        myUri=download.toString();

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");

                        String postId= reference.push().getKey();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postId", postId);
                        hashMap.put("postImage", myUri);
                        hashMap.put("description",binding.description.getText().toString());
                        hashMap.put("publisher", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                        reference.child(postId).setValue(hashMap);
                        progressDialog.dismiss();

                        startActivity(new Intent(getContext(), HomeActivity.class));

                    }
                    else
                    {
                        Toast.makeText(getContext(),"Post Failed",Toast.LENGTH_SHORT).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });

        } else
        {
            Toast.makeText(getContext(),"No Image Selected" ,Toast.LENGTH_SHORT).show();
        }

    }



}