package com.example.signupactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class FragmentProfile extends Fragment {

    private static final String TAG = "FragmentProfile";

    ImageView mProfilePic;
    Button mEditProfile;
    TextView mFullName, mEmail, mCountry, mCity, mAddress, mGender;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    StorageReference mProfileRef;
    String UID, urlPic;
    String fullName, email, country, city, streetAddress, gender;
    boolean isProfilePic = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);


        mProfilePic = view.findViewById(R.id.frag_prof_pic);
        mEditProfile = view.findViewById(R.id.frag_prof_edit_profile);
        mFullName = view.findViewById(R.id.frag_prof_fullname);
        mEmail = view.findViewById(R.id.frag_prof_email);
        mCountry = view.findViewById(R.id.frag_prof_country);
        mCity = view.findViewById(R.id.frag_prof_city);
        mAddress = view.findViewById(R.id.frag_prof_address);
        mGender = view.findViewById(R.id.frag_prof_gender);
        progressBar = view.findViewById(R.id.frag_prof_progressbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        UID = firebaseUser.getUid();


        mRef = mDatabase.getReference("Users/" + UID);
        mRef.keepSynced(true);
        mProfileRef = FirebaseStorage.getInstance().getReference("profilepics/" + UID + "/profilepicture.jpg");




        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        progressBar.setVisibility(View.VISIBLE);
        new AsyncData().execute();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, 3000);
    }

    private void getData()
    {
        progressBar.setVisibility(View.VISIBLE);
        try
        {

            mRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {

                    Log.d(TAG, "onDataChange: FragmentProfile" + dataSnapshot);
                    Log.d(TAG, "onDataChange.getValue : " + dataSnapshot.getValue());

                    Log.d(TAG, "onDataChange: Boolean Data " + String.valueOf(Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfile").getValue()))));

                    if(Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfile").getValue())))
                    {
                        fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                        email = String.valueOf(dataSnapshot.child("email").getValue());
                        country=String.valueOf(dataSnapshot.child("Profile").child("country").getValue());
                        city=String.valueOf(dataSnapshot.child("Profile").child("city").getValue());
                        streetAddress=String.valueOf(dataSnapshot.child("Profile").child("address").getValue());
                        gender=String.valueOf(dataSnapshot.child("Profile").child("gender").getValue());
                    }
                    else
                    {
                        fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                        email = String.valueOf(dataSnapshot.child("email").getValue());
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

    private void updateUI()
    {

        mFullName.setText(fullName);
        mEmail.setText(email);
        mCountry.setText(country);
        mCity.setText(city);
        mAddress.setText(streetAddress);
        mGender.setText(gender);



        if(isProfilePic)
        {
            mProfileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {


                    Picasso.get()
                            .load(uri)
                            .placeholder(R.drawable.person_black_18dp)
                            .into(mProfilePic);

                }
            });

        }
        else
        {
            mProfilePic.setImageResource(R.drawable.person_black_18dp);
        }


        progressBar.setVisibility(View.GONE);


    }


    private class AsyncData extends AsyncTask<Void, Void, Void>
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
