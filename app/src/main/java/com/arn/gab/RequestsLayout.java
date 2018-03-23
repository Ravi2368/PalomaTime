package com.arn.gab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RequestsLayout extends AppCompatActivity {

    private TextView namerequest;
    private CircleImageView imageRequest;
    private Button acceptRequest,rejectRequest;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mRequests;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_layout);

        final String user_id = getIntent().getStringExtra("user_id");

        namerequest = (TextView)findViewById(R.id.request_name);
        imageRequest = (CircleImageView) findViewById(R.id.request_image);
        acceptRequest = (Button) findViewById(R.id.request_accept);
        rejectRequest = (Button) findViewById(R.id.request_reject);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mRequests = FirebaseDatabase.getInstance().getReference().child("Requests");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                namerequest.setText(display_name);
                Picasso.with(RequestsLayout.this).load(image).placeholder(R.drawable.default_image).into(imageRequest);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mRequests.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(RequestsLayout.this, "Rejected", Toast.LENGTH_SHORT).show();

                                        Intent mainIntent = new Intent(RequestsLayout.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                });

                            }
                        });
                    }
                });

            }
        });

        acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                Map friendsMap = new HashMap();
                friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                friendsMap.put("Friends/" + user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);


                friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
                friendsMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);

                friendsMap.put("Requests/" + mCurrent_user.getUid() + "/" + user_id, null);


                mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        if(databaseError == null){

                            Toast.makeText(RequestsLayout.this, "Accepted", Toast.LENGTH_SHORT).show();

                            Intent mainIntent = new Intent(RequestsLayout.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        } else {

                            String error = databaseError.getMessage();

                            Toast.makeText(RequestsLayout.this, error, Toast.LENGTH_SHORT).show();

                            Intent mainIntent = new Intent(RequestsLayout.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }

                    }
                });

            }
        });

    }
}
