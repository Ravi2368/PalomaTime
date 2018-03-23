package com.arn.gab;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    EditText mStatus;
    Button mSaveStatus;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private ProgressDialog mProgess;

    EditText nameEdit,statusEdit,numberEdit,dobEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        nameEdit = (EditText)findViewById(R.id.compose_name);
        statusEdit = (EditText)findViewById(R.id.statusEdited);
        numberEdit = (EditText)findViewById(R.id.compose_number);
        dobEdit = (EditText)findViewById(R.id.compose_dob);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = mFirebaseUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String number = dataSnapshot.child("number").getValue().toString();
                String dob = dataSnapshot.child("DOB").getValue().toString();

                nameEdit.setText(name);
                statusEdit.setText(status);
                numberEdit.setText(number);
                dobEdit.setText(dob);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSaveStatus = (Button)findViewById(R.id.saveStatus);

        /*

            mProgess.dismiss();
                            Intent i = new Intent(StatusActivity.this,SettingsActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

         */

        mSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgess = new ProgressDialog(StatusActivity.this);
                mProgess.setTitle("Updating Details");
                mProgess.setMessage("Please wait your status is updating");
                mProgess.setCanceledOnTouchOutside(false);
                mProgess.show();

                final String newName = nameEdit.getText().toString();
                String newStatus = statusEdit.getText().toString();
                final String newNumber = numberEdit.getText().toString();
                final String newDob = dobEdit.getText().toString();

                mDatabaseReference.child("status").setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            mDatabaseReference.child("name").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        mDatabaseReference.child("number").setValue(newNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){

                                                    mDatabaseReference.child("DOB").setValue(newDob).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){

                                                                mProgess.dismiss();
                                                                Intent i = new Intent(StatusActivity.this,SettingsActivity.class);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(i);
                                                                finish();

                                                            }

                                                        }
                                                    });

                                                }

                                            }
                                        });

                                    }

                                }
                            });

                        }

                        else {

                            Toast.makeText(StatusActivity.this, "Some error Occurs", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }
        });

    }
}
