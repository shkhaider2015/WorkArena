package com.example.signupactivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView mMATextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mMATextView = findViewById(R.id.mainActivity_textview);



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null)
        {
            mMATextView.setVisibility(View.GONE);
            FragmentSignUp mSignUp = new FragmentSignUp();
            fragmentTransaction.replace(R.id.fragment_container, mSignUp);
            fragmentTransaction.commit();

        }
        else
        {
            mMATextView.setVisibility(View.GONE);
            FragmentSignIn mSignIn = new FragmentSignIn();
            fragmentTransaction.replace(R.id.fragment_container, mSignIn);
            fragmentTransaction.commit();

        }
    }
}
