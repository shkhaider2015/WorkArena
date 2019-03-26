package com.example.signupactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener , RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "EditProfileActivity";
    private static final int CHOOSE_IMAGE = 101;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
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

    long value = 0;
    String fullName="", email="", phoneNumber="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Initialize Firebase Objects
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        if(mDatabase == null)
        {
            mDatabase.setPersistenceEnabled(true);
        }
        mRef = mDatabase.getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("Profile");
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


        new StartSync().execute();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                updateUIFromRoot();

            }
        }, 2000);


    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.confirm_profile1:
                new UploadData().execute();
                break;
            case R.id.profilepic_profile1:
                showImageChooser();
                break;
        }

    }

    private void uploadUserInformation()
    {
        final String fullName, email, country, city, address, phoneNumber;
        final String URL;
        URL = URL_ProfilePicture;


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
            try
            {

                mRef.child("full name").setValue(fullName);
                mRef.child("email").setValue(email);
                mRef.child("country").setValue(country);
                mRef.child("city").setValue(city);
                mRef.child("address").setValue(address);
                mRef.child("phone number").setValue(phoneNumber);
                mRef.child("gender").setValue(gender);
                mRef.child("profilepicurl").setValue(URL);
                mRef.child("value").setValue(1);


            }catch (Exception e)
            {
                System.out.print(e);
                Log.d(TAG, "uploadUserInformation ERROR: " +e.getMessage());
            }



        }
        else
        {
            Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
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

    private void getData()
    {
        DatabaseReference ref = mDatabase.getReference("Users/" + mAuth.getCurrentUser().getUid());

        if(!getValue())
        {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    fullName = dataSnapshot.child("full name").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    phoneNumber = dataSnapshot.child("phone number").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private boolean getValue()
    {
        DatabaseReference reference = mDatabase.getReference("Users/" + mAuth.getCurrentUser().getUid() + "/Profile/value");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                Log.d(TAG, "onDataChange: " + dataSnapshot);
                EditProfileActivity.this.value = (long) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: %s" , databaseError.toException() );

            }
        });

        if(value == 1)
            return true;

        return false;
    }

    private void updateUIFromRoot()
    {
        mFullName.setText(fullName);
        mEmail.setText(email);
        mPhoneNumber.setText(phoneNumber);
    }

    private class UploadData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {

            uploadUserInformation();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mFramProgress.setVisibility(View.GONE);
            Toast.makeText(EditProfileActivity.this, "Updated" , Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFramProgress.setVisibility(View.VISIBLE);
        }


    }

    private class StartSync extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
