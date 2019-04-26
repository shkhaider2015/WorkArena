package com.example.signupactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FragmentSignUp extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "FragmentSignUp";
    EditText mFullName, mEmail, mPassword, mPhone;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    private ProgressBar mProgressbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.sign_up, container, false);

        Button mSignUp = view.findViewById(R.id.signup_button);
        TextView mWarn2 = view.findViewById(R.id.warn2_textview);
        mFullName = view.findViewById(R.id.fullname_edittext);
        mEmail = view.findViewById(R.id.email_edittext);
        mPassword = view.findViewById(R.id.password_edittext);
        mPhone = view.findViewById(R.id.phone_edittext);
        mProgressbar = view.findViewById(R.id.signup_progressbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        if(mDatabase == null)
        {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }


        mSignUp.setOnClickListener(this);
        mWarn2.setOnClickListener(this);

        mFullName.setOnFocusChangeListener(this);
        mEmail.setOnFocusChangeListener(this);
        mPassword.setOnFocusChangeListener(this);
        mPhone.setOnFocusChangeListener(this);




        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signup_button:

                mProgressbar.setVisibility(View.VISIBLE);
                userInfo();

                break;
            case R.id.warn2_textview:

                FragmentSignIn signIn = new FragmentSignIn();
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, signIn);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }

    }

    private void userInfo()
    {
        final String fullName , email, password, phone;

        fullName = mFullName.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        phone = mPhone.getText().toString().trim();

        if(fullName.isEmpty())
        {
            mFullName.setError(getString(R.string.fullname_required));
            mFullName.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if(fullName.length() > 20)
        {
            mFullName.setError(getString(R.string.fullname_length));
            mFullName.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }

        if(email.isEmpty())
        {
            mEmail.setError(getString(R.string.email_required));
            mEmail.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError(getString(R.string.email_incorrect));
            mEmail.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }

        if(password.isEmpty())
        {
            mPassword.setError(getString(R.string.password_required));
            mPassword.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if(password.length() < 6)
        {
            mPassword.setError(getString(R.string.password_length));
            mPassword.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if(phone.isEmpty())
        {
            mPhone.setError(getString(R.string.phone_required));
            mPhone.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }
        if(phone.length() != 11)
        {
            mPhone.setError(getString(R.string.phone_length));
            mPhone.requestFocus();
            mProgressbar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {

                        if(task.isSuccessful())
                        {
                            try {

                                    DatabaseReference usersRef = mDatabase.getReference("Users");
                                    DatabaseReference userRef = usersRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());


                                    userRef.child("full name").setValue(fullName);
                                    userRef.child("email").setValue(email);
                                    userRef.child("phone number").setValue(phone);
                                    userRef.child("isProfile").setValue(false);
                                    userRef.child("isPortfolio").setValue(false);

                                    Log.d(TAG, "Sign Up successfully " );
                                    Toast.makeText(getContext(), "Sign Up successfully", Toast.LENGTH_SHORT).show();


                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                    Objects.requireNonNull(getActivity()).finish();


                            }
                            catch (NullPointerException e)
                            {

                                Log.d(TAG, "NullPOinter Exception : " + e.getMessage());

                            }
                            catch (Exception e)
                            {

                                Log.d(TAG, "onComplete Exception: " +e.getMessage());
                            }



                        }
                        else
                        {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                            {
                                Log.d(TAG, "Email is already registered ");
                                Toast.makeText(getContext(), "Email is already registered", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else
                            {
                                Log.d(TAG, "onComplete: Error : Something goes wrong" + Objects.requireNonNull(task.getException()).getMessage());
                            }
                            mProgressbar.setVisibility(View.GONE);
                        }

                        mProgressbar.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        mProgressbar.setVisibility(View.GONE);

                        Log.d(TAG, "onFailure: "+e.getMessage());
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();

                    }
                });


    }


    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if(!hasFocus)
        {
            hideKeyboard(v);
        }

    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
