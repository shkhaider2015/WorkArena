package com.example.signupactivity;

import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.IOException;

public class EditPortfolioActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "EditPortfolioActivity";
    private final static int CHOOSE_IMAGE = 101;

    private ImageView  mProfilePic;
    private Spinner mProfession;
    private EditText mAboutUser, mFullName, mEmail, mCountry, mCity, mCompanyName;
    private TextView mUploadTimelinePic;
    private Button mUpdate;
    private ConstraintLayout mTimelinePicSet;

    private boolean isProfilePic, isTimelinePic, isProfessionSelected, updateUI;
    private Uri UriPortfolioPic;
    private String UrlPortfolioPic;
    protected String mUid;
    long value = 0;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    Uri profilepicuri;
    String fullName = null, email = null, country = null, city = null, aboutYou= null, company= null, profession=null;


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

       mAuth = FirebaseAuth.getInstance();
       mDatabase = FirebaseDatabase.getInstance();
       mStorage = FirebaseStorage.getInstance();

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
                if(updateUI)
                {
                    updateUIFromProfile();
                }
                else
                {
                    updateUIFromProfile();
                }
            }
        }, 10000);


    }

    private boolean isInformationCorreect()
    {
        String fullname, email, country, city, company, profession, aboutUser;

        fullname = mFullName.getText().toString(); email = mEmail.getText().toString(); country = mCountry.getText().toString();
        city = mCity.getText().toString(); company = mCompanyName.getText().toString();
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
                Toast.makeText(this, "Profile Pic Clicked", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.portfolio_edit_upload_timeline_pic:
                Toast.makeText(this, "Timeline Text Clicked", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.portfolio_edit_update_button:
                Toast.makeText(this, "update button Clicked", Toast.LENGTH_SHORT)
                        .show();
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

                            }
                        });

                isProfilePic = true;


            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void getData()
    {
        if(getPortfolioValue())
        {
            String mUrl = "Users/" + mUid + "portfolio";
            DatabaseReference mRef = mDatabase.getReference(mUrl);
            if(mUrl == null)
            {
                Log.d(TAG, "getData: Url is null");
                return;
            }
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Log.d(TAG, "onDataChange: " + dataSnapshot);
                    profilepicuri =(Uri) dataSnapshot.child("profilepicuri").getValue();
                    aboutYou = dataSnapshot.child("description").getValue().toString();
                    fullName = dataSnapshot.child("full name").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    country = dataSnapshot.child("country").getValue().toString();
                    city = dataSnapshot.child("city").getValue().toString();
                    updateUI =true;


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }
        else
        {
            String mUrl = "Users/" + mUid + "/Profile";
            DatabaseReference mRef = mDatabase.getReference(mUrl);

            if(mUrl == null || mRef == null)
            {
                Log.d(TAG, "getData: Url is null");
                return;
            }


            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Log.d(TAG, "getData from profile :: " + dataSnapshot);
                    fullName = dataSnapshot.child("full name").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    country = dataSnapshot.child("country").getValue().toString();
                    city = dataSnapshot.child("city").getValue().toString();
                    profilepicuri = Uri.parse(dataSnapshot.child("profilepicurl").getValue().toString());
                    updateUI = false;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: %s", databaseError.toException());

                }
            });
        }

    }

    private boolean getPortfolioValue()
    {
        String mUrl = "Users/" + mUid + "/Portfolio";
        DatabaseReference valueRef = mDatabase.getReference(mUrl);

        if(valueRef == null)
            return false;


        valueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "getProfileValue : " + dataSnapshot);
                EditPortfolioActivity.this.value =(long) dataSnapshot.child("value").getValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d(TAG, "getProfileValue : " + databaseError.getMessage());

            }
        });

        if(value == 1)
            return true;

        return false;
    }

    private void updateUIFromProfile()
    {
        Picasso.get()
                .load(profilepicuri)
                .placeholder(R.drawable.person_black_18dp)
                .into(mProfilePic);
        mFullName.setText(fullName);
        mEmail.setText(email);
        mCountry.setText(country);
        mCity.setText(city);
    }

    private void updateUIFromPortfolio()
    {
        Picasso.get()
                .load(profilepicuri)
                .placeholder(R.drawable.person_black_18dp)
                .into(mProfilePic);

        mFullName.setText(fullName);
        mEmail.setText(email);
        mCountry.setText(country);
        mCity.setText(city);
        mAboutUser.setText(aboutYou);
        mCompanyName.setText(company);
    }

    private void loadData()
    {
        DatabaseReference reference = mDatabase.getReference("Users/" + mAuth.getCurrentUser().getUid() + "/Portfolio");

        if(isProfilePic)
        {
            DatabaseReference reference1 = mDatabase.getReference("Users/" + mAuth.getCurrentUser().getUid() + "/Profile/profilepicurl");
            reference1.setValue(UrlPortfolioPic);
        }
        if(!isInformationCorreect())
        {
            return;
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
        reference.child("value").setValue(1);

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

}
