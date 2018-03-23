package com.arn.gab;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Security extends AppCompatActivity {

    private Button one,two,three,four,five,six,seven,eight,nine,backSpace,zero,enter;
    private String password,sqlPassword;
    private TextView writtenPassword,forgotPassword;

    SQLiteDatabase sqLiteDatabase;
    UserDBRequests userDBRequests;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        password = "";

        one = (Button)findViewById(R.id.click_one);
        two = (Button)findViewById(R.id.click_two);
        three = (Button)findViewById(R.id.click_three);
        four = (Button)findViewById(R.id.click_four);
        five = (Button)findViewById(R.id.click_five);
        six = (Button)findViewById(R.id.click_six);
        seven = (Button)findViewById(R.id.click_seven);
        eight = (Button)findViewById(R.id.click_eight);
        nine = (Button)findViewById(R.id.click_nine);
        backSpace = (Button)findViewById(R.id.click_back);
        zero = (Button)findViewById(R.id.click_zero);
        enter = (Button)findViewById(R.id.click_enter);

        writtenPassword = (TextView)findViewById(R.id.written_password);
        forgotPassword = (TextView)findViewById(R.id.forgot_password);

        writtenPassword.setText(password);

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

        int length = parts_code.length;

        sqlPassword = parts_code[length-1];

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "1";
                writtenPassword.setText(password);

            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "2";
                writtenPassword.setText(password);

            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "3";
                writtenPassword.setText(password);

            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "4";
                writtenPassword.setText(password);

            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "5";
                writtenPassword.setText(password);

            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "6";
                writtenPassword.setText(password);

            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "7";
                writtenPassword.setText(password);

            }
        });

        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "8";
                writtenPassword.setText(password);

            }
        });

        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "9";
                writtenPassword.setText(password);

            }
        });

        backSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (password.length()>0) {
                    StringBuffer sb = new StringBuffer(password);
                    sb = sb.deleteCharAt(password.length() - 1);
                    password = sb.toString();
                    writtenPassword.setText(password);

                }
            }
        });

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = password + "0";
                writtenPassword.setText(password);

            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String wrotePass = writtenPassword.getText().toString();

                if (wrotePass.equals(sqlPassword)){

                    Intent i = new Intent(Security.this,MainActivity.class);
                    startActivity(i);

                }
                else {

                    Toast.makeText(Security.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}
