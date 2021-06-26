package com.chatapp.example.flamingoapp.phase2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.chatapp.example.flamingoapp.adapters.ChatAdapter;
import com.chatapp.example.flamingoapp.models.MessageModel;
import com.chatapp.example.flamingoapp.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.nl.smartreply.SmartReply;
import com.google.mlkit.nl.smartreply.SmartReplyGenerator;
import com.google.mlkit.nl.smartreply.SmartReplySuggestion;
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult;
import com.google.mlkit.nl.smartreply.TextMessage;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityChatDetailBinding;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import id.zelory.compressor.Compressor;

import static com.chatapp.example.flamingoapp.adapters.ChatAdapter.conversation;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;
    ProgressDialog dialog;
    String senderId, receiverId, senderRoom, receiverRoom;
    Uri imageUri;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    byte[] new_image;
    SmartReplyGenerator smartReplyGenerator;
    String smart = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        smartReplyGenerator = SmartReply.getClient();

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");

        auth = FirebaseAuth.getInstance();
        senderId = auth.getUid();     // taking user id from firebase first  // using final to make variable global

        // sender and receiver id
        receiverId = getIntent().getStringExtra("userId");  // taking
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);   // uploading to chatDetails
        Picasso.get().load(profilePic).placeholder(R.drawable.user2).into(binding.profileimage);


        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, ChatListActivity.class);
                startActivity(intent);
            }
        });


        binding.smartReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smart.equals("true")) {
                    smart = "false";
                    Toast.makeText(ChatDetailActivity.this, "Smart Reply Off", Toast.LENGTH_SHORT).show();
                    binding.smartReply.setBackgroundResource(R.drawable.sw);
                    binding.reply1.setVisibility(View.INVISIBLE);
                    binding.reply2.setVisibility(View.INVISIBLE);
                    binding.reply3.setVisibility(View.INVISIBLE);
                } else {
                    smart = "true";
                    Toast.makeText(ChatDetailActivity.this, "Smart Reply On", Toast.LENGTH_SHORT).show();
                    binding.smartReply.setBackgroundResource(R.drawable.sb);
                }


                HashMap<String, Object> obj = new HashMap<>();
                obj.put("smartReply", smart);

                database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .updateChildren(obj);
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();    // data coming from MessageModel

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, receiverId);
        binding.chatRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);  // linear layout in recycler view
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        database.getReference().child("Users").child(Objects.requireNonNull(auth.getUid())).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        assert user!= null;
                        if(user.getSmartReply().equals("true"))
                        {
                            smart = "true";
                            binding.smartReply.setBackgroundResource(R.drawable.sb);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //for showing online offline
        database.getReference().child("presence").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()) {
                        binding.userStatus.setText(status);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // working on showing message in recyclerView
        database.getReference().child("chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();                       // showing one msg only once
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());   //get message id to delete
                            //
                            messageModels.add(model);                  // adding msgs from firebase

                        }
                        chatAdapter.notifyDataSetChanged();            // updating recyclerView continues
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


        final Handler handler = new Handler();
        binding.etMessage.addTextChangedListener(new TextWatcher() {   // to show typing status
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (conversation.size() != 0 && smart.equals("true")) {
                    SmartReply();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (conversation.size() != 0 && smart.equals("true")) {
                    SmartReply();

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("presence").child(senderId).setValue("Typing...");   // if typing then show typing else show online
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStopTyping, 1000);       // 1000  =  1 second
            }

            Runnable userStopTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderId).setValue("Online");
                }
            };
        });

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message2 = binding.etMessage.getText().toString();
                if (binding.etMessage.getText().toString().isEmpty()) {
                    binding.etMessage.setError("Enter the message");
                    return;
                }

                String message = null;
                try {
                    message = encrypt(message2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Date date = new Date();
                final MessageModel model = new MessageModel(senderId, message, date.getTime());   // taking sender msg and id
                model.setTime(new Date().getTime());
                binding.etMessage.setText("");                                 // empty editText after send msg


                database.getReference().child("chats").child(senderRoom)   // sender node work
                        .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // receiver node work  ,,, message will send on both sides , sender and receiver and editText also get empty
                        database.getReference().child("chats")
                                .child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
        });

        binding.reply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etMessage.setText(binding.reply1.getText());
            }
        });

        binding.reply2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etMessage.setText(binding.reply2.getText());
            }
        });

        binding.reply3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etMessage.setText(binding.reply3.getText());
            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatDetailActivity.this);
            }
        });

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                                 // camera
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatDetailActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {                     // getting image from gallery
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            //  image compression

            File actualImage = new File(imageUri.getPath());
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

            // binding.showProfilePic.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Something gone wrong !!", Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadPost(byte[] final_image) {
        if (imageUri != null) {
            Calendar calendar = Calendar.getInstance();
            final StorageReference reference = storage.getReference().child("pictures").child(calendar.getTimeInMillis() + "");

            uploadTask = reference.putBytes(new_image);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {   // taking back image url
                            @Override
                            public void onSuccess(Uri uri) {
                                String filePath = uri.toString();
                                String message = binding.etMessage.getText().toString();
                                Date date = new Date();
                                final MessageModel model = new MessageModel(senderId, message, date.getTime());   // taking sender msg and id
                                model.setTime(new Date().getTime());
                                model.setMessage("Photoz");                                  // to check image is sent
                                model.setImageUrl(filePath);
                                binding.etMessage.setText("");                                      // empty editText after send msg

                                database.getReference().child("chats").child(senderRoom)               // sender node work
                                        .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // receiver node work  ,,, message will send on both sides , sender and receiver and editText also get empty
                                        database.getReference().child("chats")
                                                .child(receiverRoom).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                    }
                                });


                            }
                        });/////////

                    }
                }
            });
        } else {
            Toast.makeText(ChatDetailActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }

    }

    public String encrypt(String data) throws Exception {
        String AES = "AES";
        String password = "terminator";
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;

    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public String decrypt(String data) throws Exception {
        String AES = "AES";
        String password = "terminator";
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(data, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }


    public void SmartReply() {
        smartReplyGenerator.suggestReplies(conversation).addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
            @Override
            public void onSuccess(SmartReplySuggestionResult smartReplySuggestionResult) {
                if (smartReplySuggestionResult.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {

                } else if (smartReplySuggestionResult.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                    String reply = "";
                    for (SmartReplySuggestion suggestion : smartReplySuggestionResult.getSuggestions()) {

                        reply = reply + suggestion.getText() + "#";

                        String r = "";
                        String s[] = new String[3];
                        int n = 0;
                        char c;
                        for (int i = 0; i < reply.length() - 1; i++) {
                            c = reply.charAt(i);
                            if (c != '#') {
                                r += c;
                            } else {
                                s[n] = r;
                                r = "";
                                n++;
                            }
                        }
                        s[n] = r;
                        r = "";

                        binding.reply1.setVisibility(View.VISIBLE);
                        binding.reply2.setVisibility(View.VISIBLE);
                        binding.reply3.setVisibility(View.VISIBLE);


                        binding.reply1.setText(s[0]);
                        binding.reply2.setText(s[1]);
                        binding.reply3.setText(s[2]);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });

    }
}
