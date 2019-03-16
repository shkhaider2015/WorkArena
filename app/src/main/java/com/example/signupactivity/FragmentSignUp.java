package com.example.signupactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentSignUp extends Fragment implements View.OnClickListener {

    EditText mFullName, mEmail, mPassword, mPhone;

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
                break;
            case R.id.warn2_textview:
                break;
        }

    }

    private void userInfo()
    {
        String fullName , email, password, phone;

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

    }



}
