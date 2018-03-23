package com.arn.gab;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SecuritySetup extends AppCompatActivity {

    private EditText pass,confirmPass,hint;
    private Button securitySave;

    Context context = this;
    UserDBRequests userDbRequests;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_setup);

        pass = (EditText)findViewById(R.id.first_password);
        confirmPass = (EditText)findViewById(R.id.confirm_password);
        hint = (EditText)findViewById(R.id.some_hint);

        securitySave = (Button)findViewById(R.id.security_save);
        
        securitySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String passStr = pass.getText().toString();
                String confirmPassStr = confirmPass.getText().toString();
                String hintStr = hint.getText().toString();
                
                if (passStr.isEmpty()){

                    Toast.makeText(SecuritySetup.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                    return;
                    
                }

                if (confirmPassStr.isEmpty()){

                    Toast.makeText(SecuritySetup.this, "Please enter Confirm password", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (hintStr.isEmpty()){

                    Toast.makeText(SecuritySetup.this, "Please enter Some hint", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (!(passStr.equals(confirmPassStr))){

                    Toast.makeText(SecuritySetup.this, "Password and Confirm Password Should be Same", Toast.LENGTH_SHORT).show();
                    return;

                }

                userDbRequests = new UserDBRequests(context);
                sqLiteDatabase = userDbRequests.getWritableDatabase();
                userDbRequests.addInformations("1",passStr,hintStr,sqLiteDatabase);
                Toast.makeText(context, "Lock is set", Toast.LENGTH_SHORT).show();
                userDbRequests.close();

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);

            }
        });
        
    }
}