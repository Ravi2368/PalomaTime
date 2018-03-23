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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import static com.arn.gab.R.id.userNameLogin;

public class LoginFrame extends AppCompatActivity {

    EditText usernameLogin,passwordLogin;

    Button loginButton;

    private Toolbar mToolbar;

    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;

    private ProgressDialog mLoginProgress;

    Context context = this;
    UserDBRequests userDbRequests;
    SQLiteDatabase sqLiteDatabase;
    UserDBNotification userDbNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_frame);

        mAuth = FirebaseAuth.getInstance();

        /*

        mToolbar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Paloma");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        */

        mLoginProgress = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        usernameLogin = (EditText)findViewById(userNameLogin);
        passwordLogin = (EditText)findViewById(R.id.passwordLogin);
        loginButton = (Button)findViewById(R.id.login);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String userNameLoginString = usernameLogin.getText().toString();
                String passWordLoginString = passwordLogin.getText().toString();

                if (!userNameLoginString.isEmpty() || !passWordLoginString.isEmpty()){

                    mLoginProgress.setTitle("Logging in");
                    mLoginProgress.setMessage("Please wait while we checking your details");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(userNameLoginString,passWordLoginString);

                }

            }
        });

    }

    private void loginUser(String userNameLogin, String passWordLogin) {

        mAuth.signInWithEmailAndPassword(userNameLogin,passWordLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    String current_user_id = mAuth.getCurrentUser().getUid();

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            userDbNotification = new UserDBNotification(context);
                            sqLiteDatabase = userDbNotification.getWritableDatabase();
                            userDbNotification.addInformations("1","Shown",sqLiteDatabase);
                           // Toast.makeText(getApplicationContext(),"Inserted", Toast.LENGTH_LONG).show();
                            userDbNotification.close();

                            Intent mainIntent = new Intent(LoginFrame.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                    });

                }
                else {
                    mLoginProgress.hide();

                    Toast.makeText(LoginFrame.this, "Sorry some error occurs", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void goToRegistering(View view) {

        Intent registeIntent = new Intent(LoginFrame.this,RegistrationFrame.class);
        startActivity(registeIntent);

    }
}