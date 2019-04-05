package com.example.signupactivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import static android.support.constraint.Constraints.TAG;

public class FragmentPortfolio extends Fragment {

    View mPortfolioHeader, mPortfolioDescription, mPortfolioPersonalInfo, mPortfolioFeedback;

    ProgressBar progressBar;
    // Header data
    TextView hFullName, hProfession; ImageView hProfilePic;
    //Description Data
    TextView dDescription, dValue, dCondition; Button dCall;
    //Personal Info Data
    TextView pFullName, pEmail, pCountry, pCity, pProfession, pCompanyName;
    //Feedback Data
    EditText fFeedback; Button fConfirm;

    boolean isProfilePic = false;

    String mUID;

    FirebaseAuth mAuth; FirebaseDatabase mDatabase;

    String fullName ="", profession ="", urlProfilePic , description ="", email ="", country ="", city ="", companyName="";
    boolean condition=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portfolio_fragment, container, false);

        progressBar = view.findViewById(R.id.portfolio_fragment_progressbar);

        mPortfolioHeader = view.findViewById(R.id.portfolio_header);
        mPortfolioDescription = view.findViewById(R.id.portfolio_description);
        mPortfolioPersonalInfo = view.findViewById(R.id.portfolio_personal_info);
        mPortfolioFeedback = view.findViewById(R.id.portfolio_feedback);

        //Header Initialization
        hFullName = mPortfolioHeader.findViewById(R.id.portfolio_header_full_name);
        hProfession = mPortfolioHeader.findViewById(R.id.portfolio_header_profession);
        hProfilePic = mPortfolioHeader.findViewById(R.id.portfolio_header_profile_pic);

        //Description Data
        dDescription = mPortfolioDescription.findViewById(R.id.portfolio_description_description);
        dValue = mPortfolioDescription.findViewById(R.id.portfolio_description_value);
        dCondition = mPortfolioDescription.findViewById(R.id.portfolio_description_condition);
        dCall = mPortfolioDescription.findViewById(R.id.portfolio_description_button);

        //Personal Info Initialize
        pFullName = mPortfolioPersonalInfo.findViewById(R.id.portfolio_personal_info_fullname);
        pEmail = mPortfolioPersonalInfo.findViewById(R.id.portfolio_personal_info_email);
        pCountry = mPortfolioPersonalInfo.findViewById(R.id.portfolio_personal_info_country);
        pCity = mPortfolioPersonalInfo.findViewById(R.id.portfolio_personal_info_city);
        pProfession = mPortfolioPersonalInfo.findViewById(R.id.portfolio_personal_info_profession);
        pCompanyName = mPortfolioPersonalInfo.findViewById(R.id.portfolio_personal_info_company_name);

        //feedback data initialize

        fFeedback = mPortfolioFeedback.findViewById(R.id.portfolio_feedback_feedback);
        fConfirm = mPortfolioFeedback.findViewById(R.id.portfolio_feedback_submit);



        mAuth = FirebaseAuth.getInstance();
        mUID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance();
        if(mDatabase == null)
        {
            mDatabase.setPersistenceEnabled(true);
        }

        new AsyncData().execute();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                updateUI();

            }
        }, 5000);


        return view;
    }

    private void updateUI()
    {
        downloadImages();

            hFullName.setText(fullName); hProfession.setText(profession);
            dDescription.setText(description);
            dCondition.setText(String.valueOf(condition));
            pFullName.setText(fullName);
            pEmail.setText(email);
            pCountry.setText(country);
            pCity.setText(city);
            pCompanyName.setText(companyName);
            pProfession.setText(profession);

        progressBar.setVisibility(View.GONE);

    }

    private void downloadData() {

        DatabaseReference reference = mDatabase.getReference("Users/" + mUID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange FragmentProfile: datasnapshot" + dataSnapshot);

                if(Boolean.valueOf(String.valueOf(dataSnapshot.child("isPortfolio").getValue())))
                {
                    fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                    profession = String.valueOf(dataSnapshot.child("Portfolio").child("profession").getValue());
                    description = String.valueOf(dataSnapshot.child("Portfolio").child("description").getValue());
                    condition = Boolean.valueOf(String.valueOf(dataSnapshot.child("Portfolio").child("condition")));
                    companyName = String.valueOf(dataSnapshot.child("Portfolio").child("company name").getValue());
                    email = String.valueOf(dataSnapshot.child("email").getValue());
                    country = String.valueOf(dataSnapshot.child("Profile").child("country").getValue());
                    city = String.valueOf(dataSnapshot.child("Profile").child("city").getValue());
                }
                else if(Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfile").getValue())))
                {
                    fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                    email = String.valueOf(dataSnapshot.child("email").getValue());
                    country = String.valueOf(dataSnapshot.child("Profile").child("country").getValue());
                    city = String.valueOf(dataSnapshot.child("Profile").child("city").getValue());
                }
                else
                {
                    fullName = String.valueOf(dataSnapshot.child("full name").getValue());
                    email = String.valueOf(dataSnapshot.child("email").getValue());
                }

                isProfilePic = Boolean.valueOf(String.valueOf(dataSnapshot.child("isProfilePic").getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: %s", databaseError.toException());
            }
        });

        reference.keepSynced(true);
    }


    private void downloadImages()
    {
        try
        {
            StorageReference reference = FirebaseStorage.getInstance().getReference("profilepics/" + mUID + "/profilepicture.jpg");

            Log.d(TAG, "downloadImages: " + reference);


            if(isProfilePic)
            {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get()
                                .load(String.valueOf(uri))
                                .placeholder(R.drawable.person_black_18dp)
                                .into(hProfilePic);
                    }
                });
            }
            else
            {
                hProfilePic.setImageResource(R.drawable.person_black_18dp);
            }

        }catch (Exception e)
        {
            Log.e(TAG, "downloadImages: %s", e);
        }
    }

    private class AsyncData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            downloadData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
