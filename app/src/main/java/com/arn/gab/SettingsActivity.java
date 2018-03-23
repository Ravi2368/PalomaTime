package com.arn.gab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mcurrentUser;

    private ProgressDialog mProgressDialog;

    CircleImageView circleImageView;
    TextView nameTextView,statusTextView;

    private StorageReference mImageStore;

    ImageButton editOptionButton,changeImageButton;

    TextView settingsEmail,settingsNumber,settingsDOB;

    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        circleImageView = (CircleImageView)findViewById(R.id.settings_image);
        nameTextView = (TextView)findViewById(R.id.settings_name);
        statusTextView = (TextView)findViewById(R.id.settings_status);
        settingsEmail = (TextView)findViewById(R.id.setting_email);
        settingsNumber = (TextView)findViewById(R.id.setting_mobile);
        settingsDOB = (TextView)findViewById(R.id.setting_dob);
        editOptionButton = (ImageButton)findViewById(R.id.edit_details);
        changeImageButton = (ImageButton)findViewById(R.id.change_image);

        mImageStore = FirebaseStorage.getInstance().getReference();
        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mcurrentUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String number = dataSnapshot.child("number").getValue().toString();
                String dob = dataSnapshot.child("DOB").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                nameTextView.setText(name);
                statusTextView.setText(status);
                settingsEmail.setText(email);
                settingsNumber.setText(number);
                settingsDOB.setText(dob);

                if (!image.equals("default")){

                    // Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_image).into(circleImageView);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_image).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_image).into(circleImageView);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");

                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);

            }
        });

        editOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SettingsActivity.this,StatusActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == RESULT_OK) {

            mProgressDialog = new ProgressDialog(SettingsActivity.this);
            mProgressDialog.setTitle("Uploading Image");
            mProgressDialog.setMessage("Please wait while we uploading your profile");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            Uri resultUri = data.getData();
            final String encodeImages = resultUri.toString();

            // New Works stars here

            File thumb_filepath = new File(resultUri.getPath());

            String currentUserId = mcurrentUser.getUid();

            StorageReference filepath = mImageStore.child("profile_images").child(currentUserId +".jpg");

            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){


                        @SuppressWarnings("VisibleForTests")String download_url = task.getResult().getDownloadUrl().toString();



                        mDatabaseReference.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    mProgressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Uploaded Succesfully", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                    else {

                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();

                    }

                }
            });

        }

        /*
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){

                Uri resultUri = result.getUri();

            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = result.getError();

            }

*/

    }

    public static String random(){

        Random generater = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generater.nextInt(10);
        char tempChar;
        for(int i = 0;i< randomLength;i++){

            tempChar = (char)(generater.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);

        }
        return randomStringBuilder.toString();
    }

}