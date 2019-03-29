package com.example.signupactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class EditPortfolioActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "EditPortfolioActivity";
    private final static int CHOOSE_PROFILE = 101;
    private final static int CHOOSE_TIMELINE = 102;

    private ImageView  mProfilePic;
    private Spinner mProfession;
    private EditText mAboutUser, mFullName, mEmail, mCountry, mCity, mCompanyName;
    private TextView mUploadTimelinePic;
    private Button mUpdate;
    private ConstraintLayout mTimelinePicSet;
    private ProgressBar progressBar;

    private boolean isProfilePic, isTimelinePic, isProfessionSelected, updateUI;
    private Uri UriPortfolioPic, UriTimelinePic;
    private String UrlPortfolioPic, UrlTimelinePic;
    protected String mUid;
    SingletonValue value;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;


    Uri profilepicuri;
    String fullName = null, email = null, country = null, city = null, aboutYou= null, company= null, profession=null, streetAddress="";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_portfolio);

       mProfilePic = findViewById(R.id.portfolio_edit_upload_profile_pic);
       mUploadTimelinePic = findViewById(R.id.portfolio_edit_upload_timeline_pic);
       mTimelinePicSet = findViewById(R.id.portfolio_edit_timeline);
       mUpdate = findViewById(R.id.portfolio_edit_update_button);

       mAboutUser = findViewById(R.id.portfolio_edit_about_user);
       mFullName = findViewById(R.id.portfolio_edit_full_name);
       mEmail = findViewById(R.id.portfolio_edit_email);
       mCountry = findViewById(R.id.portfolio_edit_country);
       mCity = findViewById(R.id.portfolio_edit_city);
       mCompanyName = findViewById(R.id.portfolio_edit_company_name);
       mProfession = findViewById(R.id.portfolio_edit_profession);
       progressBar = findViewById(R.id.portfolio_edit_progressbar);

       mAuth = FirebaseAuth.getInstance();
       mDatabase = FirebaseDatabase.getInstance();
       mStorage = FirebaseStorage.getInstance();
       context = getApplicationContext();
       value = SingletonValue.getInstance(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

       progressBar.setVisibility(View.VISIBLE);
       if(mDatabase == null)
       {
           mDatabase.setPersistenceEnabled(true);
       }

       mProfilePic.setOnClickListener(this);
       mUploadTimelinePic.setOnClickListener(this);
       mUpdate.setOnClickListener(this);

       mProfession.setOnItemSelectedListener(this);

       isProfilePic = false;
       isTimelinePic = false;
       isProfessionSelected = false;

       mUid = mAuth.getCurrentUser().getUid();

       new AsyncClass().execute();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUIFromPortfolio();
            }
        }, 10000);


    }

    private boolean isInformationCorreect()
    {
        String fullname, email, country, city, company, profession, aboutUser;

        fullname = mFullName.getText().toString();
        email = mEmail.getText().toString().trim();
        country = mCountry.getText().toString();
        city = mCity.getText().toString();
        company = mCompanyName.getText().toString();
        aboutUser = mAboutUser.getText().toString();

        if(fullname.isEmpty())
        {
            mFullName.setError(getString(R.string.fullname_required));
            mFullName.requestFocus();
            return false;
        }
        if(email.isEmpty())
        {
            mEmail.setError(getString(R.string.email_required));
            mEmail.requestFocus();
            return false;
        }
        if(country.isEmpty())
        {
            mCountry.setError(getString(R.string.country_empty));
            mCountry.requestFocus();
            return false;
        }
        if(city.isEmpty())
        {
            mCity.setError(getString(R.string.city_empty));
            mCity.requestFocus();
            return false;
        }
        if(company.isEmpty())
        {
            mCompanyName.setError(getString(R.string.company_empty));
            mCompanyName.requestFocus();
            return false;
        }
        if(aboutUser.isEmpty())
        {
            mAboutUser.setError(getString(R.string.about_user_empty));
            mAboutUser.requestFocus();
            return false;
        }




        return true;
    }


    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.portfolio_edit_upload_profile_pic:
                showImageChooser(CHOOSE_PROFILE);
                break;
            case R.id.portfolio_edit_upload_timeline_pic:
                showImageChooser(CHOOSE_TIMELINE);
                break;
            case R.id.portfolio_edit_update_button:
                new UploadData().execute();
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        profession = mProfession.getItemAtPosition(position).toString();
        isProfessionSelected = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        isProfessionSelected = false;
        Log.d(TAG, "onNothingSelected: Nothing Selected");

    }

    private void showImageChooser(int requestCode)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select profile pic"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode)
        {
            case CHOOSE_PROFILE:

                if(resultCode == RESULT_OK && data != null && data.getData() != null)
                {
                    UriPortfolioPic = data.getData();

                    try
                    {
                        final StorageReference storageRef = FirebaseStorage.getInstance().getReference("profilepics/"+mUid + "/profilepicture.jpg");
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), UriPortfolioPic);
                        mProfilePic.setImageBitmap(bitmap);

                        storageRef.putFile(UriPortfolioPic)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        UrlPortfolioPic = storageRef.getDownloadUrl().toString();
                                        isProfilePic = true;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: ProfilePic : " +e.getMessage());

                                    }
                                });

                        isProfilePic = true;


                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }


                break;
            case CHOOSE_TIMELINE:


                if(resultCode == RESULT_OK && data != null && data.getData() != null)
                {
                    UriTimelinePic = data.getData();

                    try
                    {
                        final StorageReference storageRef = FirebaseStorage.getInstance().getReference("timelinepic/"+mUid + "/timelinepicture.jpg");
                        Bitmap tempBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(UriTimelinePic));
                        Drawable drawable = new BitmapDrawable(getResources(), tempBitmap);
                        mTimelinePicSet.setBackground(drawable);

                        storageRef.putFile(UriTimelinePic)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        UrlTimelinePic = storageRef.getDownloadUrl().toString();
                                        isTimelinePic = true;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: ProfilePic : " +e.getMessage());

                                    }
                                });

                        isProfilePic = true;


                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private void getData()
    {
        System.out.println(value.getPortfolioValue());

        try
        {
                final String mUrl = "Users/" + mUid + "/Portfolio";
                DatabaseReference mRef = mDatabase.getReference(mUrl);

                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        Log.d(TAG, "onDataChange: " + dataSnapshot);
                        profilepicuri =Uri.parse(String.valueOf(dataSnapshot.child("profilepicuri").getValue()));
                        aboutYou = String.valueOf(dataSnapshot.child("description").getValue());
                        fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                        email = String.valueOf(dataSnapshot.child("email").getValue());
                        country = String.valueOf(dataSnapshot.child("country").getValue());
                        city = String.valueOf(dataSnapshot.child("city").getValue());

                        updateUI =true;


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }catch (Exception e)
        {
            Log.e(TAG, "getData:  %s", e);
        }






    }
    private void updateUIFromPortfolio()
    {
        StorageReference storage = FirebaseStorage.getInstance().getReference("profilepics/" + mUid + "/profilepicture.jpg");
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.person_black_18dp)
                        .into(mProfilePic);

            }
        });

        mFullName.setText(fullName);
        mEmail.setText(email);
        mCountry.setText(country);
        mCity.setText(city);
        mAboutUser.setText(aboutYou);
        mCompanyName.setText(company);

        progressBar.setVisibility(View.GONE);
    }

    private void loadData()
    {
        DatabaseReference reference = mDatabase.getReference("Users/" + Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "/Portfolio");

        if(isProfilePic)
        {
            DatabaseReference reference1 = mDatabase.getReference("Users/" + mAuth.getCurrentUser().getUid() + "/profilepicurl.jpg");
            reference1.setValue(UrlPortfolioPic);
        }

        if(!isProfessionSelected)
        {
            Toast.makeText(this, "Please select profession", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        reference.child("description").setValue(aboutYou);
        reference.child("full name").setValue(fullName);
        reference.child("email").setValue(email);
        reference.child("country").setValue(country);
        reference.child("city").setValue(city);
        reference.child("profession").setValue(profession);
        reference.child("company name").setValue(company);
        reference.child("condition").setValue(true);
        reference.child("value").setValue(1);
        reference.child("timelineuri").setValue(UriTimelinePic);

    }



    private class AsyncClass extends AsyncTask<Void, Void, Void>
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

    private class UploadData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            loadData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(EditPortfolioActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }

}
