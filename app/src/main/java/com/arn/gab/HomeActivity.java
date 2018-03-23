package com.arn.gab;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {


    private SQLiteDatabase sqLiteDatabase;
    private UserDBRequests userDBRequests;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Thread myThread = new Thread(){

            @Override
            public void run(){

                try {

                    sleep(2000);

                    userDBRequests = new UserDBRequests(getApplicationContext());
                    sqLiteDatabase = userDBRequests.getReadableDatabase();
                    cursor = userDBRequests.getInformation(sqLiteDatabase);

                    String sec_set = "";
                    String sec_code = "";
                    String sec_hint = "";

                    if (cursor.moveToFirst()) {
                        do {

                            sec_set = sec_set+cursor.getString(0)+"_";
                            sec_code = sec_code+cursor.getString(1)+"_";
                            sec_hint = sec_hint+cursor.getString(2)+"_";

                        } while ((cursor.moveToNext()));

                    }

                    String[] parts_set = sec_set.split("_");
                    String[] parts_code = sec_code.split("_");
                    String[] parts_hint = sec_hint.split("_");

                    int length = parts_set.length;

                    String sqlSet = parts_set[length-1];

                    if (sqlSet.equals("1")){

                        Intent intent = new Intent(HomeActivity.this,Security.class);
                        startActivity(intent);

                    }

                    if (sqlSet.equals("0")){

                        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                        startActivity(intent);

                    }


                }catch (InterruptedException e){

                    e.printStackTrace();

                }

            }

        };

        myThread.start();

    }
}
