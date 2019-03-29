package com.example.signupactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    DatabaseReference mProfileRef;
    String UID;
    SingletonValue value;


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
        UID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        value = SingletonValue.getInstance(UID);

        if(mDatabase == null)
        {
            mDatabase.setPersistenceEnabled(true);
        }


        mRef = mDatabase.getReference("Users/" + UID);
        mProfileRef = mRef.child("Profile");



        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        getData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


            }
        }, 2000);



        return view;
    }

    private void getData()
    {
        progressBar.setVisibility(View.VISIBLE);
        try {


            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.d(TAG, "onDataChange: " + dataSnapshot);
                    Log.d(TAG, "onDataChange.getValue : " + dataSnapshot.getValue());
                    Log.d(TAG, " Value : " + value.getProfileValue());

                    if (value.getProfileValue() == 1)
                    {
                        Uri uri = Uri.parse(Objects.requireNonNull(dataSnapshot.child("Profile").child("profilepicurl").getValue()).toString());

                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.person_black_18dp)
                                .into(mProfilePic);

                        mFullName.setText(Objects.requireNonNull(dataSnapshot.child("Profile").child("full name").getValue()).toString());
                        mEmail.setText(Objects.requireNonNull(dataSnapshot.child("Profile").child("email").getValue()).toString());
                        mCountry.setText(Objects.requireNonNull(dataSnapshot.child("Profile").child("country").getValue()).toString());
                        mCity.setText(Objects.requireNonNull(dataSnapshot.child("Profile").child("city").getValue()).toString());
                        mAddress.setText(Objects.requireNonNull(dataSnapshot.child("Profile").child("address").getValue()).toString());
                        mGender.setText(Objects.requireNonNull(dataSnapshot.child("Profile").child("gender").getValue()).toString());
                    } else if (value.getProfileValue() == 0)
                    {
                        mFullName.setText(Objects.requireNonNull(dataSnapshot.child("full name").getValue()).toString());
                        mEmail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: Error  Value =  null");
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                    Log.e(TAG, "onCancelled: %s", databaseError.toException());

                }
            });
            progressBar.setVisibility(View.GONE);

        }catch (Exception e)
        {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "getData: %s", e);
        }

        progressBar.setVisibility(View.GONE);

    }



}
