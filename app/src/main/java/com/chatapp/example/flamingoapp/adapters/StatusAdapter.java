package com.chatapp.example.flamingoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase3.StoryImageActivity;
import com.phone.DoctorAppointment.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

        ArrayList<Users> list;                                                                                           // list of users
        Context context;

        public StatusAdapter(ArrayList<Users> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_story, parent,false);     // attaching sample users to main activity
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {     // showing all users list
            final Users users = list.get(position);

                Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.user).into(holder.statusimage);      // online image taken from firebase profile pic not upload we will load drawable into image
                holder.storyUserName.setText(users.getUserName());// getting user name

            holder.statusimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(users.getStatusImage()!=null)
                    {
                        Intent intent= new Intent(context, StoryImageActivity.class);
                        intent.putExtra("story", users.getStatusImage());
                        context.startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(context.getApplicationContext(), "Story is Empty",Toast.LENGTH_SHORT).show();
                    }

                }
            });


            // setting status
           // Picasso.get().load(users.getStatusImage()).into(holder.statusimage);                               // status image

        }

        @Override
        public int getItemCount() {
            return list.size();                       // if this not given we will not get chats on screen
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView statusimage;           // for status fragment
            TextView storyUserName;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                statusimage= itemView.findViewById(R.id.storyProfilePic);
                storyUserName= itemView.findViewById(R.id.storyUsername);
            }
        }

    }


