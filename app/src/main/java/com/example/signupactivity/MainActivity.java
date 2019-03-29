package com.example.signupactivity;

import android.content.Intent;
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
    TextView mMATextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        mMATextView = findViewById(R.id.mainActivity_textview);




    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if(count == 0)
        {
            super.onBackPressed();
        }
        else
        {
            getSupportFragmentManager().popBackStack();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }
        else
        {
            mMATextView.setVisibility(View.GONE);
            FragmentSignIn mSignIn = new FragmentSignIn();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mSignIn);
            fragmentTransaction.commit();

        }
    }
}
