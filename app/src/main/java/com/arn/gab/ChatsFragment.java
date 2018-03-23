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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView sample;

    private View myView;

    private RecyclerView mChatsList;

    private DatabaseReference mChatsDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private FirebaseUser mCurrent;

    private DatabaseReference mRootRef;

    private View mMainView;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myView =  inflater.inflate(R.layout.fragment_chats, container, false);

        mChatsList=(RecyclerView)myView.findViewById(R.id.chats_list);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mCurrent = mAuth.getCurrentUser();

        if (mCurrent != null){

            mCurrent_user_id = mAuth.getCurrentUser().getUid();

            mChatsDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);

        }

        mChatsDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        mChatsList.setHasFixedSize(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return myView;
    }

    // onStart() method is here

    @Override
    public void onStart(){

        super.onStart();

        FirebaseRecyclerAdapter<Chats, ChatsViewHolder> chatsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(

                Chats.class,
                R.layout.user_single_layout,
                ChatsViewHolder.class,
                mChatsDatabase

        ) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, Chats model, int position) {

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

                                Intent chatIntent = new Intent(getContext(), ChattingActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                chatIntent.putExtra("user_name", UserName);
                                startActivity(chatIntent);

                            }
                        });

                        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {

                                CharSequence options[] = new CharSequence[]{"Yes","No"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Do you want to Delete chat");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i == 0){

                                            Map unMessageMap = new HashMap();
                                            unMessageMap.put("messages/" + mCurrent_user_id + "/" + list_user_id, null);

                                            mRootRef.updateChildren(unMessageMap, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                                    if(databaseError == null){

                                                        Toast.makeText(getContext(), "Deleted Succesfully", Toast.LENGTH_SHORT).show();

                                                    } else {

                                                        String error = databaseError.getMessage();

                                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();

                                                    }

                                                }
                                            });

                                        }

                                        if(i == 1){

                                        }

                                    }
                                });

                                builder.show();

                                return false;
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mChatsList.setAdapter(chatsRecyclerViewAdapter);

    }


    // ChatsViewHolder Class starts here

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(View itemView){

            super(itemView);

            mView = itemView;

        }

        public void setLastMessage(String Date){

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