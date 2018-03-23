package com.arn.gab;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private FirebaseUser mCurrent;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList=(RecyclerView)mMainView.findViewById(R.id.friends_list);


        mAuth = FirebaseAuth.getInstance();

        mCurrent = mAuth.getCurrentUser();

        if (mCurrent != null){

            mCurrent_user_id = mAuth.getCurrentUser().getUid();

            mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);

        }

        mFriendsDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart(){

        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.user_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                viewHolder.setDate(model.getDate());

                final String list_user_id = getRef(position).getKey();

                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String UserName = dataSnapshot.child("name").getValue().toString();
                        final String UserImage = dataSnapshot.child("image").getValue().toString();

                        if (dataSnapshot.hasChild("online")){

                            String UserOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(UserOnline);

                        }

                        viewHolder.setName(UserName);
                        viewHolder.setImage(UserImage, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i == 0){

                                            Intent profileIntent = new Intent(getContext(),Profile.class);
                                            profileIntent.putExtra("user_id",list_user_id);
                                            startActivity(profileIntent);


                                        }

                                        if(i == 1){

                                            Intent chatIntent = new Intent(getContext(),ChattingActivity.class);
                                            chatIntent.putExtra("user_id",list_user_id);
                                            chatIntent.putExtra("user_name",UserName);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });

                                builder.show();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView){

            super(itemView);

            mView = itemView;

        }

        public void setDate(String Date){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(Date);
        }

        public void setName(String name){

            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setImage(String image, Context ctx) {
            CircleImageView userSingleCircle = (CircleImageView)mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(image).placeholder(R.drawable.default_image).into(userSingleCircle);

        }

        public void setUserOnline(String onlineStatus){

            ImageView userOnlineView = (ImageView)mView.findViewById(R.id.user_single_online);

            if (onlineStatus.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            }
            else{

                userOnlineView.setVisibility(View.INVISIBLE);
            }


        }

    }

}