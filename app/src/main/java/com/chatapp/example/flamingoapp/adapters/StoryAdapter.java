package com.chatapp.example.flamingoapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.example.flamingoapp.models.Story;
import com.chatapp.example.flamingoapp.models.Users;
import com.chatapp.example.flamingoapp.phase3.AddStoryActivity;
import com.chatapp.example.flamingoapp.phase3.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phone.DoctorAppointment.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Story> mStory;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int i) {
        if (i == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story, parent, false);
            return new StoryAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sample_story, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull final ViewHolder viewHolder, final int i) {
        final Story story = mStory.get(i);


        userInfo(viewHolder, story.getUserid(), i);

        if (viewHolder.getAdapterPosition() != 0) {
            seenStory(viewHolder, story.getUserid());
        }

        if (viewHolder.getAdapterPosition() == 0) {
            myStory(viewHolder.addStoryText, viewHolder.storyPlus, false);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.getAdapterPosition() == 0) {
                    myStory(viewHolder.addStoryText, viewHolder.storyPlus, true);
                } else {
                    Intent intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("userid", story.getUserid());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

   ImageView storyPhoto, storyPlus, storyPhotoSeen;
        TextView storyUsername, addStoryText;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            storyPhoto = itemView.findViewById(R.id.storyPhoto);
            storyPlus = itemView.findViewById(R.id.addStoryButton);
            storyPhotoSeen = itemView.findViewById(R.id.storyPhotoSeen);

            storyUsername = itemView.findViewById(R.id.storyUsername);
            addStoryText = itemView.findViewById(R.id.addStoryText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    private void userInfo(final ViewHolder viewHolder, final String userId, final int pos) {
                Log.d("1 story user id"," "+userId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        // Log.d("refernce debug"," "+reference.getRoot());
        //  Log.d("refernce debug"," username: "+username+" user id: "+userid+" publisher: "+publisher);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);

                      Log.d("2 story user id"," "+user.getUserId());

//                        Picasso.get().load(user.getProfilepic()).placeholder(R.drawable.user2).into(viewHolder.storyPhoto);
//                        if(pos!=0)
//                        {
//                            Picasso.get().load(user.getProfilepic()).placeholder(R.drawable.user2).into(viewHolder.storyPhotoSeen);
//                            viewHolder.storyUsername.setText(user.getUserName());
//                        }


//                Glide.with(mContext).load(user.getProfilepic()).into(viewHolder.storyPhoto);
//                if (pos != 0) {
//                    Glide.with(mContext).load(user.getProfilepic()).into(viewHolder.storyPhotoSeen);
//                    viewHolder.storyUsername.setText(user.getUserName());
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        count++;
                    }
                }

                if (click) {
                    if (count > 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Story",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO: go to story
                                        Intent intent = new Intent(mContext, StoryActivity.class);
                                        intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }
                } else {
                    if (count > 0) {
                        textView.setText("My story");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void seenStory(final ViewHolder viewHolder, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeend()) {
                        i++;
                    }
                }

                if (i > 0) {
                    viewHolder.storyPhoto.setVisibility(View.VISIBLE);
                    viewHolder.storyPhotoSeen.setVisibility(View.GONE);
                } else {
                    viewHolder.storyPhoto.setVisibility(View.GONE);
                    viewHolder.storyPhotoSeen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
