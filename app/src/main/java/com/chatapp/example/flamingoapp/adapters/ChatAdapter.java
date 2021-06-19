package com.chatapp.example.flamingoapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.example.flamingoapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.nl.smartreply.SmartReplyGenerator;
import com.google.mlkit.nl.smartreply.TextMessage;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.ActivityChatDetailBinding;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class ChatAdapter extends RecyclerView.Adapter {
    ActivityChatDetailBinding binding;
    ArrayList<MessageModel> messageModels;
    Context context;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String recId;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    public static ArrayList<TextMessage> conversation=new ArrayList<>();


    public ChatAdapter(ArrayList<MessageModel> messageModel, Context context) {
        this.messageModels = messageModel;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> messageModels, Context context, String recId) {
        this.messageModels = messageModels;
        this.context = context;
        this.recId = recId;
    }

    @Override
    public int getItemViewType(int position) {   // checking sender or receiver
        if (messageModels.get(position).getuID().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else
            return RECEIVER_VIEW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  // inflating layouts
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MessageModel messageModel = messageModels.get(position);         // setting message positions


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {   // delete msg
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = FirebaseAuth.getInstance().getUid() + recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(messageModel.getMessageId())
                                        .setValue(null);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                return false;
            }
        });

        if (holder.getClass() == SenderViewHolder.class)     // showing sender msg
        {
            String message = null;
            try {
                message = decrypt(messageModel.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((SenderViewHolder) holder).senderMsg.setText(message);
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");                    // time
            ((SenderViewHolder) holder).senderTime.setText(dateFormat.format(new Date(messageModel.getTime())));
            if (messageModel.getMessage().equals("Photoz"))                                                        // loading image in sender view holder
            {
                SenderViewHolder viewHolder = (SenderViewHolder) holder;
                ((SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageUrl()).placeholder(R.drawable.photo).into(viewHolder.senderImage);
            }
        } else                                           // showing receiver msg
        {
            String message = null;
            try {
                message = decrypt(messageModel.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
            }
            ((ReceiverViewHolder) holder).receiverMsg.setText(message);
            if (!messageModel.getMessage().equals("Photoz")) {
                conversation.add(TextMessage.createForRemoteUser(message, System.currentTimeMillis(), messageModel.getuID()));
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");                    // time
            ((ReceiverViewHolder) holder).receiverTime.setText(dateFormat.format(new Date(messageModel.getTime())));
            if (messageModel.getMessage().equals("Photoz"))                                                          // loading image in receiver view holder
            {
                ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
                ((ReceiverViewHolder) holder).receiverImage.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.GONE);
                Picasso.get().load(messageModel.getImageUrl()).placeholder(R.drawable.photo2).into(viewHolder.receiverImage);
            }


        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;
        ImageViewZoom receiverImage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            receiverImage = itemView.findViewById(R.id.receiverImage);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        ImageViewZoom senderImage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            senderImage = itemView.findViewById(R.id.Image);
        }
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

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}
