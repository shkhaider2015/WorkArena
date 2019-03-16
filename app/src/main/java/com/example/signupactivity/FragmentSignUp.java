package com.example.signupactivity;

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
import android.widget.Button;
import android.widget.EditText;
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

public class FragmentSignUp extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentSignUp";
    EditText mFullName, mEmail, mPassword, mPhone;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_up, container, false);

        Button mSignUp = view.findViewById(R.id.signup_button);
        TextView mWarn2 = view.findViewById(R.id.warn2_textview);
        mFullName = view.findViewById(R.id.fullname_edittext);
        mEmail = view.findViewById(R.id.email_edittext);
        mPassword = view.findViewById(R.id.password_edittext);
        mPhone = view.findViewById(R.id.phone_edittext);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mSignUp.setOnClickListener(this);
        mWarn2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signup_button:

                userInfo();

                break;
            case R.id.warn2_textview:

                FragmentSignIn signIn = new FragmentSignIn();
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, signIn);
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
            return;
        }
        if(fullName.length() > 20)
        {
            mFullName.setError(getString(R.string.fullname_length));
            mFullName.requestFocus();
            return;
        }

        if(email.isEmpty())
        {
            mEmail.setError(getString(R.string.email_required));
            mEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError(getString(R.string.email_incorrect));
            mEmail.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            mPassword.setError(getString(R.string.password_required));
            mPassword.requestFocus();
            return;
        }
        if(password.length() < 6)
        {
            mPassword.setError(getString(R.string.password_length));
            mPassword.requestFocus();
            return;
        }
        if(phone.isEmpty())
        {
            mPhone.setError(getString(R.string.phone_required));
            mPhone.requestFocus();
            return;
        }
        if(phone.length() != 11)
        {
            mPhone.setError(getString(R.string.phone_length));
            mPhone.requestFocus();
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
                                    DatabaseReference userRef = usersRef.child(mAuth.getCurrentUser().getUid());


                                    userRef.child("full name").setValue(fullName);
                                    userRef.child("email").setValue(email);
                                    userRef.child("phone number").setValue(phone);

                                    DatabaseReference userProfile = userRef.child("Profile");
                                    DatabaseReference userPortfolio = userRef.child("Portfolio");

                                    userProfile.child("value").setValue(0);
                                    userPortfolio.child("value").setValue(0);

                                    Log.d(TAG, "Sign Up successfully " );
                                    Toast.makeText(getContext(), "Sign Up successfully", Toast.LENGTH_SHORT).show();


                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                    getActivity().finish();


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
                                Log.d(TAG, "onComplete: Error : Something goes wrong" + task.getException().getMessage());
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                        Log.d(TAG, "onFailure: "+e.getMessage());
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();

                    }
                });


    }



}
