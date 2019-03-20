package com.example.signupactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener , RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "EditProfileActivity";
    private static final int CHOOSE_IMAGE = 101;

    FirebaseAuth mAuth;
    DatabaseReference mRef;
    StorageReference mStorageREf;


    ImageView mProfilePicture;
    Button mConfirm;
    EditText mFullName, mEmail, mCountry, mCity, mAddress, mPhoneNumber;
    RadioGroup mGroupRadio;
    RadioButton mMale, mFemale;
    FrameLayout mFramProgress;
    String gender;
    Uri URI_ProfilePictures;
    String URL_ProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Initialize Firebase Objects
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("profile");
        mStorageREf = FirebaseStorage.getInstance().getReference("profilepics/" + mAuth.getCurrentUser().getUid() + "/profilepicture.jpg");

        Log.d(TAG, "Reference to database : " + mRef);

        //Initialize View Components
        mProfilePicture = findViewById(R.id.profilepic_profile1);
        mConfirm = findViewById(R.id.confirm_profile1);
        mFullName = findViewById(R.id.fullname_profile1);
        mEmail = findViewById(R.id.email_profile1);
        mCountry = findViewById(R.id.country_profile1);
        mCity = findViewById(R.id.city_profile1);
        mAddress = findViewById(R.id.address_profile1);
        mPhoneNumber = findViewById(R.id.phone_profile1);
        mGroupRadio = findViewById(R.id.radio_group);
        mMale = findViewById(R.id.male_radio);
        mFemale = findViewById(R.id.female_radio);
        mFramProgress = findViewById(R.id.frame_progress);

        mConfirm.setOnClickListener(this);
        mProfilePicture.setOnClickListener(this);
        mGroupRadio.setOnCheckedChangeListener(this);




    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.confirm_profile1:
                mFramProgress.setVisibility(View.VISIBLE);
                uploadUserInformation();
                break;
            case R.id.profilepic_profile1:
                showImageChooser();
                break;
        }

    }

    private void uploadUserInformation()
    {
        String fullName, email, country, city, address, phoneNumber;


        fullName = mFullName.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        country = mCountry.getText().toString().trim();
        city = mCity.getText().toString().trim();
        address = mAddress.getText().toString().trim();
        phoneNumber = mPhoneNumber.getText().toString().trim();

        if(email.isEmpty())
        {
            mEmail.setError(getString(R.string.email_required));
            mEmail.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError(getString(R.string.email_incorrect));
            mEmail.requestFocus();
            return;

        }
        if(fullName.isEmpty())
        {
            mFullName.setError(getString(R.string.fullname_required));
            mFullName.requestFocus();
            return;

        }
        if(country.isEmpty())
        {
            mCountry.setError(getString(R.string.country_empty));
            mCountry.requestFocus();
            return;

        }
        if(city.isEmpty())
        {
            mCity.setError(getString(R.string.city_empty));
            mCity.requestFocus();
            return;

        }
        if(address.isEmpty())
        {
            mAddress.setError(getString(R.string.address_empty));
            mAddress.requestFocus();
            return;

        }
        if(phoneNumber.isEmpty())
        {
            mPhoneNumber.setError(getString(R.string.phone_required));
            mPhoneNumber.requestFocus();
            return;

        }
        if(phoneNumber.length() < 11)
        {
            mPhoneNumber.setError(getString(R.string.phone_length));
            mPhoneNumber.requestFocus();
            return;
        }
        if(gender == null)
        {
            Toast.makeText(this, "Plz select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if(URL_ProfilePicture == null)
        {
            Toast.makeText(this, "Plz select profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mRef != null)
        {
            mRef.child("fullname").setValue(fullName);
            mRef.child("email").setValue(email);
            mRef.child("country").setValue(country);
            mRef.child("city").setValue(city);
            mRef.child("address").setValue(address);
            mRef.child("phone number").setValue(phoneNumber);
            mRef.child("gender").setValue(gender);
            mRef.child("profilepic").setValue(URI_ProfilePictures);

            mRef.child("value").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    mFramProgress.setVisibility(View.GONE);
                    Toast.makeText(EditProfileActivity.this, "Profile Update successfully", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId)
        {
            case R.id.male_radio:
                gender = "male";
                break;
            case R.id.female_radio:
                gender = "female";
                break;
        }

    }

    private void showImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select profile pic"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            URI_ProfilePictures = data.getData();

            try
            {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), URI_ProfilePictures);
                mProfilePicture.setImageBitmap(bitmap);
                uploadImageToFirebase();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase()
    {
        if(URI_ProfilePictures != null)
        {

            mStorageREf.putFile(URI_ProfilePictures)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mStorageREf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    URL_ProfilePicture = uri.toString();
                                    Log.d(TAG, "profile picture uri :  " + URL_ProfilePicture);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(EditProfileActivity.this, "Image not uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}
