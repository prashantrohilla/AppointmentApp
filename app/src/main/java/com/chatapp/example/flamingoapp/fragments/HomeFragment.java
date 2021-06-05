package com.chatapp.example.flamingoapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatapp.example.flamingoapp.phase2.ChatListActivity;
import com.chatapp.example.flamingoapp.phase2.HomeActivity;
import com.phone.DoctorAppointment.R;
import com.phone.DoctorAppointment.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=  FragmentHomeBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }
}