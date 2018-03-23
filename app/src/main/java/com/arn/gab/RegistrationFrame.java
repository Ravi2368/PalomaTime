package com.arn.gab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegistrationFrame extends AppCompatActivity {

    EditText userNameEdit,emailEdit,PasswordEdit,confirmPassEdit,numberEdit,dobEdit;

    Button registerButton;

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private DatabaseReference mDatabase;
    private DatabaseReference mMessageBackground;

    private ProgressDialog mRegProgress;

    Context context = this;
    UserDBRequests userDbRequests;
    SQLiteDatabase sqLiteDatabase;
    UserDBNotification userDbNotification;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_frame);

        mAuth = FirebaseAuth.getInstance();

        mRegProgress = new ProgressDialog(this);

        userNameEdit = (EditText)findViewById(R.id.userNameReg);
        emailEdit = (EditText)findViewById(R.id.emailReg);
        PasswordEdit = (EditText)findViewById(R.id.passwordReg);
        confirmPassEdit = (EditText)findViewById(R.id.confrmReg);
        numberEdit = (EditText)findViewById(R.id.numberReg);
        dobEdit = (EditText)findViewById(R.id.dobReg);

        registerButton = (Button)findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userNameString = userNameEdit.getText().toString();
                String emailString = emailEdit.getText().toString();
                String passwordString = PasswordEdit.getText().toString();
                String confirmString = confirmPassEdit.getText().toString();
                String numberString = numberEdit.getText().toString();
                String dobString = dobEdit.getText().toString();

                if (userNameString.isEmpty() || emailString.isEmpty() || passwordString.isEmpty() || confirmString.isEmpty() || numberString.isEmpty() || dobString.isEmpty()){

                    Toast.makeText(context, "All Fields are Mandiatory", Toast.LENGTH_SHORT).show();

                }

                if(!userNameString.isEmpty() && !emailString.isEmpty() && !passwordString.isEmpty() && !confirmString.isEmpty() && !numberString.isEmpty() && !dobString.isEmpty()){

                    mRegProgress.setTitle("Registering Users");
                    mRegProgress.setMessage("Please wait your account is creatng");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    registerUser(userNameString,emailString,passwordString,numberString,dobString);

                }

            }
        });

    }

    private void registerUser(final String userNameString, final String emailString, String passwordString, final String numberString, final String dobString) {

        mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();
                    String device_token_id = FirebaseInstanceId.getInstance().getToken();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("token",device_token_id);
                    userMap.put("name",userNameString);
                    userMap.put("status","Hi Buddy.. I'm using Gab app");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    userMap.put("online","false");
                    userMap.put("email",emailString);
                    userMap.put("number",numberString);
                    userMap.put("DOB",dobString);

                    mMessageBackground = FirebaseDatabase.getInstance().getReference().child("MessageBg").child(uid);

                    HashMap<String,String> MesBgMap = new HashMap<>();
                    MesBgMap.put("background","default");

                    mMessageBackground.setValue(MesBgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(RegistrationFrame.this, "Added", Toast.LENGTH_SHORT).show();

                        }
                    });

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mRegProgress.dismiss();

                                userDbRequests = new UserDBRequests(context);
                                sqLiteDatabase = userDbRequests.getWritableDatabase();
                                userDbRequests.addInformations("0","0000","ZERO",sqLiteDatabase);
                                userDbRequests.close();

                                userDbNotification = new UserDBNotification(context);
                                sqLiteDatabase = userDbNotification.getWritableDatabase();
                                userDbNotification.addInformations("1","Shown",sqLiteDatabase);
                                Toast.makeText(getApplicationContext(),"Updated", Toast.LENGTH_LONG).show();
                                userDbNotification.close();


                                Intent mainIntent = new Intent(RegistrationFrame.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });

                }
                else {
                    mRegProgress.hide();

                    Toast.makeText(RegistrationFrame.this, "Sorry some error occurs", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
