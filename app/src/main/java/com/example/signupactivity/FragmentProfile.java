package com.example.signupactivity;

import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentProfile extends Fragment {

    private static final String TAG = "FragmentProfile";

    ImageView mProfilePic;
    Button mEditProfile;
    TextView mFullName, mEmail, mCountry, mCity, mAddress;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    String UID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);


        mProfilePic = view.findViewById(R.id.profilepic_imageview);
        mEditProfile = view.findViewById(R.id.edit_profile);
        mFullName = view.findViewById(R.id.fullname_edittext);
        mEmail = view.findViewById(R.id.email_edittext);
        mCountry = view.findViewById(R.id.country);
        mCity = view.findViewById(R.id.city);
        mAddress = view.findViewById(R.id.address);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        UID = mAuth.getCurrentUser().getUid();
        mRef = mDatabase.getReference("Users/" + UID);

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Edit Profile Clikcked", Toast.LENGTH_SHORT).show();
            }
        });

        getData();

        synchronized (view)
        {
            try {
                Log.d(TAG, "View is : waiting ..........");
                view.wait(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private void getData()
    {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Long profile = (Long) dataSnapshot.child("Profile").child("value").getValue();
                Log.d(TAG, "onDataChange: Profile : " + profile);

                mFullName.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                mEmail.setText(String.valueOf(dataSnapshot.child("full name").getValue()));

                if(profile == 0)
                {
                    Log.d(TAG, "onDataChange: profile is null");
                    mCountry.setText("Country : " + String.valueOf(profile));
                    mCity.setText("City : " + String.valueOf(profile));
                    mAddress.setText("Address : " + String.valueOf(profile));
                }

                if(profile != 0)
                {
                    Log.d(TAG, "onDataChange: profile is not null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
