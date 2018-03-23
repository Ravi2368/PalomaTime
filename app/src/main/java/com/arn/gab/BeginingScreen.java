package com.arn.gab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BeginingScreen extends AppCompatActivity {

    Button beginLoginButton,beginNewAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begining_screen);

        beginLoginButton = (Button)findViewById(R.id.beginLogin);
        beginNewAccountButton = (Button)findViewById(R.id.beginNewAccount);

        beginNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registeIntent = new Intent(BeginingScreen.this,RegistrationFrame.class);
                startActivity(registeIntent);

            }
        });

        beginLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent loginIntent = new Intent(BeginingScreen.this,LoginFrame.class);
                startActivity(loginIntent);

            }
        });

    }
}