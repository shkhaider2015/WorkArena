package com.example.signupactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentSignIn extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentSignIn";
    EditText mEmail, mPassword;
    FrameLayout mProgress;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_in, container, false);

        Button mSignIn = view.findViewById(R.id.signin_button);
        TextView mWarn1 = view.findViewById(R.id.warn1_textview);
        mEmail = view.findViewById(R.id.email_edittext);
        mPassword = view.findViewById(R.id.password_edittext);
        mProgress = view.findViewById(R.id.fram_progress_sign_in);

        mAuth = FirebaseAuth.getInstance();

        mSignIn.setOnClickListener(this);
        mWarn1.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.signin_button:
                mProgress.setVisibility(View.VISIBLE);
                userInfo();

                break;
            case R.id.warn1_textview:

                FragmentSignUp signUp = new FragmentSignUp();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, signUp);
                transaction.commit();

                break;
        }

    }


    private void userInfo()
    {
        String email, password;

        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();

        if(email.isEmpty())
        {
            mEmail.setError(getString(R.string.email_required));
            mEmail.requestFocus();
            mProgress.setVisibility(View.GONE);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError(getString(R.string.email_incorrect));
            mEmail.requestFocus();
            mProgress.setVisibility(View.GONE);
            return;
        }

        if(password.isEmpty())
        {
            mPassword.setError(getString(R.string.password_required));
            mPassword.requestFocus();
            mProgress.setVisibility(View.GONE);
            return;
        }
        if(password.length() < 6)
        {
            mPassword.setError(getString(R.string.password_length));
            mPassword.requestFocus();
            mProgress.setVisibility(View.GONE);
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.d(TAG, "Sign in onComplete : " + task);
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "Sign in Successfully ", Toast.LENGTH_SHORT).show();
                            mProgress.setVisibility(View.GONE);
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.d(TAG, "Sign in onFailure : " + e.getMessage());
                        mProgress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }



}
