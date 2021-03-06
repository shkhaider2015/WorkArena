package com.example.android_firebasecustomprofile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private EditText mFullName, mEmail, mPassword, mPhoneNo;
    private Button mUpdate;
    private ImageView mProfilePic;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mReference;
    FirebaseUser firebaseUser;
    String photoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mFullName = findViewById(R.id.fullname_profile);
        mEmail = findViewById(R.id.email_profile);
        mPassword = findViewById(R.id.password_profile);
        mPhoneNo = findViewById(R.id.phoneno_profile);
        mUpdate = findViewById(R.id.update_profile);
        mProfilePic = findViewById(R.id.profile_pic_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //Intent getIntent = getIntent();
        //photoURL = getIntent.getStringExtra("photoURL");


        loadProfile();


    }

    private void downloadImage() {

        StorageReference ref = FirebaseStorage.getInstance().getReference("profilepics/");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                mProfilePic.setImageURI(uri);
                Log.d(TAG, "onSuccess: Image uri --> " + uri);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Image uri " + e.getMessage());

                    }
                });


    }

    private void loadProfile()
    {
        DatabaseReference databaseReference = mFirebaseDatabase.getReference("Users");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                final User user = dataSnapshot.getValue(User.class);

                mFullName.setText(user.mFullName);
                mEmail.setText(user.mEmail);
                mPhoneNo.setText(user.mPhoneNo);
                mPassword.setText("********");



                if(mAuth.getCurrentUser() != null)
                {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.common_full_open_on_phone);


                                GlideApp.with(UserProfileActivity.this)
                                        .load(mAuth.getCurrentUser().getPhotoUrl())
                                        .apply(requestOptions)
                                        .into(mProfilePic);


                    Log.d(TAG, "onChildAdded: " + mAuth.getCurrentUser().getPhotoUrl() );



                }




            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
                                                                                                                                                                                                                                   