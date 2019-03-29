package com.example.signupactivity;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class SingletonValue {

    private static SingletonValue value = null;
    private long profileValue;
    private long portfolioValue;

    private SingletonValue(String mUID)
    {
        fetchProfile(mUID);
        fetchProfile(mUID);
    }

    public static SingletonValue getInstance(String mUID)
    {
        if(mUID != null )
        {

            if(value == null)
            {
                return value = new SingletonValue(mUID);
            }
            else
            {
                return value;
            }

        }
        Log.d(TAG, "getInstance: mUID clashes -------------->>");
        return value;
    }

    private void fetchProfile(String mUID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/"
        + mUID
        + "/Profile");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "onDataChange: Datasnapshot " +dataSnapshot.getValue());
                profileValue = (long) dataSnapshot.child("value").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());

            }
        });
    }

    private void fetchPortfolio(String mUID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users/"
                + mUID
                + "/Profile/value");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                portfolioValue = (long) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());

            }
        });
    }

    public void setNull()
    {
        profileValue = 0;
        portfolioValue = 0;
        value = null;
    }

    public void setProfileValue(long value)
    {
        this.profileValue = value;
    }
    public void setPortfolioValue(long value)
    {
        this.portfolioValue = value;
    }
    public long getProfileValue()
    {
        return profileValue;
    }
    public long getPortfolioValue()
    {
        return portfolioValue;
    }

}
