package com.example.signupactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener , RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "EditProfileActivity";
    private static final int CHOOSE_IMAGE = 101;
    private static final int IMAGE_MAX_SIZE = 200;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    StorageReference mStorageREf;


    ImageView mProfilePicture;
    Button mConfirm;
    EditText  mCountry, mCity, mAddress;
    TextView mFullName, mEmail, mPhoneNumber;
    RadioGroup mGroupRadio;
    RadioButton mMale, mFemale;
    FrameLayout mFramProgress;
    String gender;
    Uri URI_ProfilePictures;
    String URL_ProfilePicture;
    boolean isProfilePic;

    String country="", city="", address="";
    String fullName="", Email="", Phone="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Initialize Firebase Objects
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        String mUid =String.valueOf(mAuth.getCurrentUser().getUid());
        if(mDatabase == null)
        {
            mDatabase.setPersistenceEnabled(true);
        }
        mRef = mDatabase.getReference("Users")
                .child(mUid)
                .child("Profile");
        mStorageREf = FirebaseStorage.getInstance().getReference("profilepics/" + mAuth.getCurrentUser().getUid() + "/profilepicture.jpg");
        mRef = mDatabase.getReference("Users/" + mAuth.getCurrentUser().getUid());


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
                    updateUIFromProfile();
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
        String lcountry, lcity, laddress;


        lcountry = mCountry.getText().toString().trim();
        lcity = mCity.getText().toString();
        laddress = mAddress.getText().toString();

        if(mRef != null)
        {
            try
            {

                mRef.child("Profile").child("country").setValue(lcountry);
                mRef.child("Profile").child("city").setValue(lcity);
                mRef.child("Profile").child("address").setValue(laddress);
                mRef.child("Profile").child("gender").setValue(gender);
                mRef.child("isProfile").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: Updated");
                    }
                });

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
                                    setProfilePicCondition();
                                    Log.d(TAG, "profile picture uri :  " + URL_ProfilePicture);

                                    Toast.makeText(EditProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT)
                                            .show();

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

    private void setProfilePicCondition()
    {
        DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference("Users/" + mAuth.getCurrentUser().getUid() + "/isProfilePic");

        reference.setValue(true);
    }

    private void getData()
    {
       try
       {
               mRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                   {
                       if(Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfile").getValue())))
                       {
                           fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                           Email = String.valueOf(dataSnapshot.child("email").getValue());
                           country = String.valueOf(dataSnapshot.child("Profile").child("country").getValue());
                           city = String.valueOf(dataSnapshot.child("Profile").child("city").getValue());
                           Phone = String.valueOf(dataSnapshot.child("phone number").getValue());
                           address = String.valueOf(dataSnapshot.child("Profile").child("address").getValue());
                       }
                       else
                       {
                           fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                           Email = String.valueOf(dataSnapshot.child("email").getValue());
                           Phone = String.valueOf(dataSnapshot.child("phone number").getValue());
                       }

                       isProfilePic = Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfilePic").getValue()));

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError)
                   {
                       Log.e(TAG, "onCancelled: %s", databaseError.toException());

                   }
               });







       }catch (Exception e)
       {
           Log.e(TAG, "getData: %s", e);
       }

    }


    private void updateUIFromProfile()
    {
        mFullName.setText(fullName);
        mEmail.setText(Email);
        mPhoneNumber.setText(Phone);
        mCountry.setText(country);
        mCity.setText(city);
        mAddress.setText(address);

        if(isProfilePic)
        {
            StorageReference reference = FirebaseStorage
                    .getInstance()
                    .getReference("profilepics/" + mAuth.getCurrentUser().getUid() + "/profilepicture.jpg");

            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri)
                {
                    Picasso.get()
                            .load(uri)
                            .placeholder(R.drawable.person_black_18dp)
                            .into(mProfilePicture);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Log.d(TAG, "onFailure: " + e.getMessage());

                }
            });
        }
        else
        {
            mProfilePicture.setImageResource(R.drawable.person_black_18dp);
        }
    }

    private Bitmap decodeImage(File f)
    {
        Bitmap b = null;
        try
        {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();


        }catch (FileNotFoundException fnfe)
        {
            Log.d(TAG, "decodeImage: Exception File Not Found : " +fnfe.getMessage());

        }catch (IOException ioe)
        {
            Log.d(TAG, "decodeImage: Input Output Exception : " + ioe.getMessage());

        }catch (Exception e)
        {
            Log.d(TAG, "decodeImage: Exception : " + e.getMessage());

        }

        return b;
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
            Toast.makeText(EditProfileActivity.this, "Updated" , Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
