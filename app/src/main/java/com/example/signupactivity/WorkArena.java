package com.example.signupactivity;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class WorkArena extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
