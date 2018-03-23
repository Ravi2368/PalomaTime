package com.arn.gab;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    RelativeLayout layout;

    String mChatUser;
    String mChatUserName;
    String mCurrentUserId;

    EditText writtenText;
    ImageButton sendImageButton;
    ImageButton addImageButton;

    TextView profileName;
    TextView lastSeenStatus;
    CircleImageView profileImage;

    RecyclerView messagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private FirebaseUser chatCurrentUser;
    private DatabaseReference chatUserRef;

    private DatabaseReference mMessageBg;

    private static final int TOTAL_MSG_TO_LOAD = 15;
    private int currentPage = 1;

    private final List<Messages> messageListNew = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        layout = (RelativeLayout)findViewById(R.id.chatting_frame);
        // layout.setBackgroundResource(R.drawable.messagebg);

        mToolbar = (Toolbar)findViewById(R.id.chatting);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        chatCurrentUser = mAuth.getCurrentUser();


        if (chatCurrentUser != null){

            chatUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mMessageBg = FirebaseDatabase.getInstance().getReference().child("MessageBg").child(mAuth.getCurrentUser().getUid());

        }

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id");
        mChatUserName = getIntent().getStringExtra("user_name");

        // getSupportActionBar().setTitle(mChatUserName);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflator.inflate(R.layout.custom_chat_user, null);

        actionBar.setCustomView(action_bar_view);

        profileImage = (CircleImageView)findViewById(R.id.custom_bar_image);
        profileName = (TextView)findViewById(R.id.custom_bar_title);
        lastSeenStatus = (TextView)findViewById(R.id.custom_bar_seen);

        writtenText = (EditText)findViewById(R.id.written_text);
        writtenText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(writtenText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        sendImageButton = (ImageButton)findViewById(R.id.send_button);

        mAdapter = new MessageAdapter(messageListNew);

        messagesList = (RecyclerView)findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);

        mLinearLayout = new LinearLayoutManager(this);
        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(mLinearLayout);

        messagesList.setAdapter(mAdapter);

        loadMessages();

        // loadBackgroundImage();

        profileName.setText(mChatUserName);

        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                setImage(image,getApplicationContext());

                if (online.equals("true")){

                    lastSeenStatus.setText("online");

                }
                else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);

                    String lastTimeOnline = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());

                    lastSeenStatus.setText(lastTimeOnline);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(mChatUser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();


                writtenText.setText("");

            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                currentPage++;

                messageListNew.clear();

                loadMessages();

            }
        });

    }



    private void loadBackgroundImage() {

        mMessageBg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String bg = dataSnapshot.child("background").getValue().toString();

                if(bg.equals("default")){

                    layout.setBackgroundResource(R.drawable.file5);

                }
                else if(bg.equals("1")){

                    layout.setBackgroundResource(R.drawable.file24);

                }
                else if(bg.equals("2")){

                    layout.setBackgroundResource(R.drawable.file25);

                }
                else if(bg.equals("3")){

                    layout.setBackgroundResource(R.drawable.file27);

                }
                else if(bg.equals("4")){

                    layout.setBackgroundResource(R.drawable.file18);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_MSG_TO_LOAD);

        mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                messageListNew.add(message);
                mAdapter.notifyDataSetChanged();

                messagesList.scrollToPosition(messageListNew.size()-1);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void sendMessage() {

        String message = writtenText.getText().toString();

        if (!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_path = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();

            String push_id = user_message_path.getKey();

            Date date = new Date();

            int hour = date.getHours();
            int minutes = date.getMinutes();
            int hourInTwelve = (hour%12);
            String hourTwelveString;
            String minitesInString;

            if(hour != 12) {

                hourInTwelve = (hour%12);
                hourTwelveString = String.valueOf(hourInTwelve);

            }
            else {
                hourTwelveString = String.valueOf(hour);
            }

            if (minutes < 10){

                String minitesWithNonZero = String.valueOf(minutes);
                minitesInString = "0"+minitesWithNonZero;

            }
            else {

                minitesInString = String.valueOf(minutes);

            }

            String combinationTime = hourTwelveString + ":" + minitesInString;

            Map messageMap = new HashMap();

            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);
            messageMap.put("senttime", combinationTime);

            // Work on date comes here


            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id,messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id,messageMap);
            messageUserMap.put("MessageNotification/" + mChatUser + "/" + mCurrentUserId + "/id", mCurrentUserId);
            messageUserMap.put("MessageNotification/" + mChatUser + "/" + mCurrentUserId + "/status", "notShown");

            try {

                Uri notification = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/"+R.raw.message_send);//Here is FILE_NAME is the name of file that you want to play
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();

            } catch (Exception e) {
                e.printStackTrace();
            }

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("Chat_log",databaseError.getMessage().toString());

                    }

                }
            });

        }

    }

    @Override
    public void onStop(){

        super.onStop();

        if(chatCurrentUser != null){

            chatUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if(chatCurrentUser != null){

            chatUserRef.child("online").setValue("true");

        }

    }

    public void setImage(String image, Context ctx) {

        Picasso.with(ctx).load(image).placeholder(R.drawable.default_image).into(profileImage);

    }

    public void changeMessageBackground(View view) {



        Intent bgIntent = new Intent(ChattingActivity.this,BackgroundsActivity.class);
        bgIntent.putExtra("user_id", mChatUser);
        bgIntent.putExtra("user_name", mChatUserName);
        startActivity(bgIntent);

    }

}