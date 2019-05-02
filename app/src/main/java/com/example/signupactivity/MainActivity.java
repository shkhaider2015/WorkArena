package com.example.signupactivity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
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

        Log.d(TAG, "onBackPressed: " + count);
        int mod = count % 2;

        if(count == 0)
        {

            //super.onBackPressed();
            super.onBackPressed();
        }
        else if(mod == 1)
        {
            super.onBackPressed();
        }
        else
        {
            getSupportFragmentManager().popBackStack();

        }
        /*
        if(fragmentManager.getClass().getSimpleName() == "FragmentSignUp")
        {
            Log.d(TAG, "onBackPressed: its signUp");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new FragmentSignIn(), "Sign in");
            transaction.commit();

        }
        else
        {
            Log.d(TAG, "onBackPressed: its signIn");
            finish();

        }
        */
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
            fragmentTransaction.addToBackStack(mSignIn.getClass().getSimpleName());



            if(!mSignIn.isAdded())
            {
                fragmentTransaction.add(R.id.fragment_container, mSignIn);
                //fragmentTransaction.replace(R.id.fragment_container, mSignIn);
                fragmentTransaction.commit();
            }
            else
            {
                fragmentTransaction.show(mSignIn).commit();
            }

        }
    }
}
