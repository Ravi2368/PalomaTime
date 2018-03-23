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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private CircleImageView requestCircleImageView;
    private TextView requestProfileName;
    private Button requestAcceptButton;
    private Button requestRejectButton;

    private RecyclerView mRequestsList;

    private View mRequestsView;

    private FirebaseAuth mAuth;
    private DatabaseReference mRequestsDatabase;
    private DatabaseReference mRequestsDatabaseMain;
    private DatabaseReference mUserDatabase;

    private String mCurrent_user_id;

    private FirebaseUser mCurrent;

    private DatabaseReference mRootRef;

    private Button acceptButton,rejectButton;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRequestsView =  inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestsList = (RecyclerView)mRequestsView.findViewById(R.id.requests_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrent = mAuth.getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        if (mCurrent != null){

            mCurrent_user_id = mAuth.getCurrentUser().getUid();

            mRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");

            completeReq();

        }

        return mRequestsView;

    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> requestsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(

                Requests.class,
                R.layout.user_single_request,
                RequestsViewHolder.class,
                mRequestsDatabaseMain

        ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, Requests model, int position) {

                final String list_user_id = getRef(position).getKey();

                Toast.makeText(getContext(), list_user_id, Toast.LENGTH_SHORT).show();

                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String UserName = dataSnapshot.child("name").getValue().toString();
                        final String UserImage = dataSnapshot.child("image").getValue().toString();

                        /*

                        if (dataSnapshot.hasChild("online")){

                            String UserOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(UserOnline);

                        }  */

                        viewHolder.setName(UserName);
                        viewHolder.setImage(UserImage, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent profileIntent = new Intent(getContext(),RequestsLayout.class);
                                profileIntent.putExtra("user_id",list_user_id);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mRequestsList.setAdapter(requestsRecyclerViewAdapter);

    }

    private void completeReq() {

        mRequestsDatabaseMain = FirebaseDatabase.getInstance().getReference().child("Requests").child(mCurrent_user_id);

        mRequestsDatabaseMain.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RequestsViewHolder(View itemView){

            super(itemView);

            mView = itemView;

        }

        public void setName(String name){

            TextView userNameView = (TextView)mView.findViewById(R.id.user_request_name);
            userNameView.setText(name);

        }

        public void setImage(String image, Context ctx) {
            CircleImageView userSingleCircle = (CircleImageView)mView.findViewById(R.id.circleRequest);

            Picasso.with(ctx).load(image).placeholder(R.drawable.default_image).into(userSingleCircle);

        }

    }

    /*

    public void acceptFriendRequest(View view) {

        if(mCurrent_state.equals("req_received")){

            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            Map friendsMap = new HashMap();
            friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + acpt_user_id + "/date", currentDate);
            friendsMap.put("Friends/" + acpt_user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);


            friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + acpt_user_id, null);
            friendsMap.put("Friend_req/" + acpt_user_id + "/" + mCurrent_user.getUid(), null);

            friendsMap.put("Requests/" + mCurrent_user.getUid() + "/" + acpt_user_id, null);


            mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if(databaseError == null){

                        mProfileSendReqBtn.setEnabled(true);
                        mCurrent_state = "friends";
                        mProfileSendReqBtn.setText("Unfriend this Person");

                        mDeclineBtn.setVisibility(View.INVISIBLE);
                        mDeclineBtn.setEnabled(false);

                    } else {

                        String error = databaseError.getMessage();

                        Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }



    }

    public void rejecetFriendRequest(View view) {

        mFriendReqDatabase.child(mCurrent_user.getUid()).child(acpt_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFriendReqDatabase.child(acpt_user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mRequests.child(acpt_user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(Profile.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Send Request");
                                mDeclineBtn.setVisibility(View.INVISIBLE);

                            }
                        });

                    }
                });
            }
        });
    }  */

}