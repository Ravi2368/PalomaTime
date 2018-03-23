package com.arn.gab;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static io.reactivex.internal.schedulers.SchedulerPoolFactory.start;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private FrameLayout mViewPager;
    private SectionsPageAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef,mRequests;
    private DatabaseReference mRootRef;


    private DatabaseReference mMessageNotificatonShowing;

    private TabLayout mTabLayout;

    Context context = this;
    UserDBNotification userDbNotification;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Paloma");

        if (mAuth.getCurrentUser() != null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

        //Tabs
        mViewPager = (FrameLayout) findViewById(R.id.main_tabPager);
        // mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // mViewPager.setAdapter(mSectionsPagerAdapter);

       // mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
       // mTabLayout.setupWithViewPager(mViewPager);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {

                    case R.id.action_requests:

                        RequestsFragment rf = new RequestsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_tabPager,rf).commit();

                        break;

                    case R.id.action_chats:

                        ChatsFragment cf = new ChatsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_tabPager,cf).commit();

                        break;

                    case R.id.action_friends:

                        FriendsFragment ff = new FriendsFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_tabPager,ff).commit();

                        break;


                }

                return true;

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();

        } else {

            mUserRef.child("online").setValue("true");

            ChatsFragment cff = new ChatsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_tabPager,cff).commit();

            mRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
            mRequests.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){

                        mRequests.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (final DataSnapshot dsp : dataSnapshot.getChildren()){

                                    final String mUsedOnce = String.valueOf(dsp.getKey());
                                    String id = dataSnapshot.child(mUsedOnce).child("id").getValue().toString();

                                    String stat = dataSnapshot.child(mUsedOnce).child("status").getValue().toString();

                                    if (stat.equals("notShown")){

                                        compareComplete(id,"friend");

                                        mRequests.child(mAuth.getCurrentUser().getUid()).child(mUsedOnce).child("status").setValue("shown");
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            messageNotify();
           // checkMessageNotification();

            /*

            Thread myThread = new Thread(){

                public void run(){

                    while (true){

                        try {

                            try {

                                messageNotify();
                                checkMessageNotification();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            sleep(3000);

                        }catch (InterruptedException ie){

                            ie.printStackTrace();

                        }
                    }
                }

            };

            myThread.start();
            */

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            /*

            Thread myThread = new Thread(){

                public void run(){

                    while (true){

                        try {
                            sleep(3000);

                            try {

                                messageNotify();
                                checkMessageNotification();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }catch (InterruptedException ie){

                            ie.printStackTrace();

                        }
                    }
                }

            };

            myThread.start();

            */

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, LoginFrame.class);
        startActivity(startIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if(item.getItemId() == R.id.main_settings_btn){

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if(item.getItemId() == R.id.main_all_btn){

            Intent settingsIntent = new Intent(MainActivity.this, UsersList.class);
            startActivity(settingsIntent);

        }

        return true;
    }

    private void compareComplete(final String id, final String toWhat) {

        DatabaseReference mRefForCompare = FirebaseDatabase.getInstance().getReference().child("Users");

        mRefForCompare.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child(id).child("name").getValue().toString();

                if (toWhat.equals("friend")){

                    NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification noti = new Notification.Builder
                            (getApplicationContext()).setContentTitle("Friend Request").setContentText("Sent you a Friend Request").
                            setContentTitle(name).setSmallIcon(R.drawable.paloma_logo).build();

                    noti.sound =Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/"+R.raw.huawei_notify_tune);//Here is FILE_NAME is the name of file that you want to play

                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;

                    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                    notif.notify(0, noti);

                }
                else if (toWhat.equals("message")){

                    NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification noti = new Notification.Builder
                            (getApplicationContext()).setContentTitle("New Message").setContentText("Sent you a Message").
                            setContentTitle(name).setSmallIcon(R.drawable.paloma_logo).build();

                    noti.sound =Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/"+R.raw.huawei_notify_tune);//Here is FILE_NAME is the name of file that you want to play

                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;

                    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                    notif.notify(0, noti);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void messageNotify(){

        mMessageNotificatonShowing = FirebaseDatabase.getInstance().getReference().child("MessageNotification");

        mMessageNotificatonShowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

              //  Toast.makeText(context, "Here main", Toast.LENGTH_SHORT).show();

                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {

                    final int c = 0;

                    mMessageNotificatonShowing.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                          //  Toast.makeText(MainActivity.this, "First here", Toast.LENGTH_SHORT).show();

                            for (final DataSnapshot dsp : dataSnapshot.getChildren() ) {

                                final String mUsedOnce = String.valueOf(dsp.getKey());

                                final String id = dataSnapshot.child(mUsedOnce).child("id").getValue().toString();

                                String stat = dataSnapshot.child(mUsedOnce).child("status").getValue().toString();

                                if (stat.equals("notShown")){

                                    userDbNotification = new UserDBNotification(context);
                                    sqLiteDatabase = userDbNotification.getWritableDatabase();
                                    boolean isUpdate = userDbNotification.updateData("1",stat,sqLiteDatabase);
                                  //  Toast.makeText(getApplicationContext(),"Updated", Toast.LENGTH_LONG).show();
                                    userDbNotification.close();

                                    Map msgNotifMap = new HashMap();
                                    msgNotifMap.put("MessageNotification/" + mAuth.getCurrentUser().getUid() + "/" + mUsedOnce, null);

                                    mRootRef.updateChildren(msgNotifMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                            if(databaseError == null){

                                             //   Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

                                                checkMessageNotification(id);

                                            } else {

                                                String error = databaseError.getMessage();

                                             //   Toast.makeText(context, error, Toast.LENGTH_SHORT).show();


                                            }

                                        }
                                    });

                                }break;

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else{

                  //  Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_SHORT).show();

                }

                return;

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void checkMessageNotification(String id){

       // Toast.makeText(context, "I came here", Toast.LENGTH_SHORT).show();

        userDbNotification = new UserDBNotification(getApplicationContext());
        sqLiteDatabase = userDbNotification.getReadableDatabase();
        cursor = userDbNotification.getInformation(sqLiteDatabase);

        String com_id = "";
        String com_status = "";

        if (cursor.moveToFirst()) {

            do {
                com_id = com_id+cursor.getString(0);
                com_status = com_status+cursor.getString(1);

            } while ((cursor.moveToNext()));
        }

        if (com_status.equals("Shown"))
        {
          //  Toast.makeText(this,"No Messages", Toast.LENGTH_SHORT).show();
        }
        else {

            compareComplete(id,"message");

          //  Toast.makeText(context, "You have some messages", Toast.LENGTH_SHORT).show();

            userDbNotification = new UserDBNotification(context);
            sqLiteDatabase = userDbNotification.getWritableDatabase();
            boolean isUpdate = userDbNotification.updateData("1","Shown",sqLiteDatabase);
            userDbNotification.close();

        }

    }

}