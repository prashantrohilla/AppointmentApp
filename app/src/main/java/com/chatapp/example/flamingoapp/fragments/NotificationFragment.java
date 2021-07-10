package com.chatapp.example.flamingoapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.example.flamingoapp.adapters.MyPhotoAdapter;
import com.chatapp.example.flamingoapp.adapters.NotificationAdapter;
import com.chatapp.example.flamingoapp.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.FragmentNotificationBinding;
import com.phone.DoctorAppointment.databinding.FragmentProfileBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    public NotificationFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=  FragmentNotificationBinding.inflate(inflater, container, false);
        binding.notificationRecyclerView.setHasFixedSize(true);
        LinearLayoutManager saveLayoutManager = new LinearLayoutManager(getContext());
        binding.notificationRecyclerView.setLayoutManager(saveLayoutManager);

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        binding.notificationRecyclerView.setAdapter(notificationAdapter);

        readNotifications();
        return binding.getRoot();

    }


    private void readNotifications()
    {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               notificationList.clear();
               for(DataSnapshot snapshot1:snapshot.getChildren())
               {
                   Notification notification=snapshot1.getValue(Notification.class);
                   notificationList.add(notification);
               }

                Collections.reverse(notificationList);
               notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}