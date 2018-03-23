package com.arn.gab;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersList extends AppCompatActivity {

    private RecyclerView usersList;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private String receiver_name;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mToolbar = (Toolbar)findViewById(R.id.userList_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Paloma Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        usersList = (RecyclerView)findViewById(R.id.users_list);

        // mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(

                Users.class,R.layout.user_single_layout,UserViewHolder.class,mDatabaseReference

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getImage(),getApplicationContext());

                final String user_id = getRef(position).getKey();
                final String user_name = model.getName();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UsersList.this,Profile.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);

                    }
                });

            }
        };

        usersList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name) {

            TextView userSingleName = (TextView)mView.findViewById(R.id.user_single_name);
            userSingleName.setText(name);

        }

        public void setStatus(String status) {
            TextView userSingleStatus = (TextView)mView.findViewById(R.id.user_single_status);
            userSingleStatus.setText(status);
        }

        public void setImage(String image, Context ctx) {
            CircleImageView userSingleCircle = (CircleImageView)mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(image).placeholder(R.drawable.default_image).into(userSingleCircle);

        }
    }

}
