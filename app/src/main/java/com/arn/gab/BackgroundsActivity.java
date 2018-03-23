package com.arn.gab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BackgroundsActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseBackG;
    private FirebaseAuth mAuth;

    String mChatUser;
    String mChatUserName;
    String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgrounds);

        mChatUser = getIntent().getStringExtra("user_id");
        mChatUserName = getIntent().getStringExtra("user_name");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){

            mDatabaseBackG = FirebaseDatabase.getInstance().getReference().child("MessageBg").child(mAuth.getCurrentUser().getUid());

        }
    }

    public void backGround5(View view) {

        mDatabaseBackG.child("background").setValue("5");
        Intent i = new Intent(BackgroundsActivity.this,ChattingActivity.class);
        i.putExtra("user_id", mChatUser);
        i.putExtra("user_name", mChatUserName);
        startActivity(i);

    }

    public void backGround4(View view) {

        mDatabaseBackG.child("background").setValue("4");
        Intent i = new Intent(BackgroundsActivity.this,ChattingActivity.class);
        i.putExtra("user_id", mChatUser);
        i.putExtra("user_name", mChatUserName);
        startActivity(i);

    }

    public void backGround3(View view) {

        mDatabaseBackG.child("background").setValue("3");
        Intent i = new Intent(BackgroundsActivity.this,ChattingActivity.class);
        i.putExtra("user_id", mChatUser);
        i.putExtra("user_name", mChatUserName);
        startActivity(i);

    }

    public void backGround2(View view) {

        mDatabaseBackG.child("background").setValue("2");
        Intent i = new Intent(BackgroundsActivity.this,ChattingActivity.class);
        i.putExtra("user_id", mChatUser);
        i.putExtra("user_name", mChatUserName);
        startActivity(i);

    }

    public void backGround1(View view) {

        mDatabaseBackG.child("background").setValue("1");
        Intent i = new Intent(BackgroundsActivity.this,ChattingActivity.class);
        i.putExtra("user_id", mChatUser);
        i.putExtra("user_name", mChatUserName);
        startActivity(i);

    }

}