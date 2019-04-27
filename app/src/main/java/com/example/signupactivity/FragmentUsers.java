package com.example.signupactivity;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FragmentUsers extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentUsers";

    View V_header, V_description, V_personalInfo, V_feedback;
    ProgressBar mProgressBar;

    ImageView H_profilePic; TextView H_fullName, H_profession;
    Button D_call; TextView D_userDescription, D_userCondition;
    TextView P_name, P_email, P_country, P_city, P_profession, P_companyName;
    Button F_submit; EditText F_feedback;

    String userID; Uri uri;

    String name="", email="", profession="", country="", city="", companyName="", userDescription="", condition= "off";

    DatabaseReference reference;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_fragment, container, false);

        userID = getArguments().getString("userID");

        getUserData();
        mProgressBar = view.findViewById(R.id.users_list_progressbar);
        mProgressBar.setVisibility(View.VISIBLE);

        V_header = view.findViewById(R.id.users_list_header);
        V_description = view.findViewById(R.id.users_list_description);
        V_personalInfo = view.findViewById(R.id.users_list_personal_info);
        V_feedback = view.findViewById(R.id.users_list_feedback);

        H_profilePic = V_header.findViewById(R.id.portfolio_header_profile_pic);
        H_fullName = V_header.findViewById(R.id.portfolio_header_full_name);
        H_profession = V_header.findViewById(R.id.portfolio_header_profession);

        D_call = V_description.findViewById(R.id.portfolio_description_button);
        D_userDescription = V_description.findViewById(R.id.portfolio_description_description);
        D_userCondition = V_description.findViewById(R.id.portfolio_description_condition);

        P_name = V_personalInfo.findViewById(R.id.portfolio_personal_info_fullname);
        P_email = V_personalInfo.findViewById(R.id.portfolio_personal_info_email);
        P_country = V_personalInfo.findViewById(R.id.portfolio_personal_info_country);
        P_city = V_personalInfo.findViewById(R.id.portfolio_personal_info_city);
        P_companyName = V_personalInfo.findViewById(R.id.portfolio_personal_info_company_name);
        P_profession = V_personalInfo.findViewById(R.id.portfolio_personal_info_profession);

        F_submit = V_feedback.findViewById(R.id.portfolio_feedback_submit);
        F_feedback = V_feedback.findViewById(R.id.portfolio_feedback_feedback);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                updateUI();
                mProgressBar.setVisibility(View.GONE);
            }
        }, 5000);


        F_submit.setOnClickListener(this);

        return view;
    }

    private void getUserData()
    {
        reference = FirebaseDatabase.getInstance().getReference("Users/" + userID);
        String storageRef = "profilepics/" + userID + "/profilepicture.jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(storageRef);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "onDataChange: " + dataSnapshot);

                name = String.valueOf(dataSnapshot.child("full name").getValue());
                email = String.valueOf(dataSnapshot.child("email").getValue());
                profession = String.valueOf(dataSnapshot.child("Portfolio").child("profession").getValue());
                condition = String.valueOf(dataSnapshot.child("Portfolio").child("value").getValue());
                companyName = String.valueOf(dataSnapshot.child("Portfolio").child("company name").getValue());
                country = String.valueOf(dataSnapshot.child("Profile").child("country").getValue());
                city = String.valueOf(dataSnapshot.child("Profile").child("city").getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                FragmentUsers.this.uri = uri;
            }
        });

    }

    private void updateUI()
    {
        H_fullName.setText(name); H_profession.setText(profession);
        D_userDescription.setText(userDescription); D_userCondition.setText(condition);
        P_name.setText(name); P_email.setText(email); P_country.setText(country);
        P_city.setText(city); P_companyName.setText(companyName);
        P_profession.setText(profession);

        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.person_black_18dp)
                .into(H_profilePic);
    }

    private void handleFeedback()
    {
        mAuth = FirebaseAuth.getInstance();
        String uID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String feedback = F_feedback.getText().toString();

        if(feedback.isEmpty())
        {
            F_feedback.setError("write something");
            F_feedback.requestFocus();
            return;
        }

        reference.child("Comments").child(uID).setValue(feedback);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.portfolio_feedback_submit:
                handleFeedback();
                break;
        }
    }
}
