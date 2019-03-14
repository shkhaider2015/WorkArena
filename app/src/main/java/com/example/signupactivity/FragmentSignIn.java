package com.example.signupactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentSignIn extends Fragment implements View.OnClickListener {

    EditText mEmail, mPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_in, container, false);

        Button mSignIn = view.findViewById(R.id.signin_button);
        TextView mWarn1 = view.findViewById(R.id.warn1_textview);
        mEmail = view.findViewById(R.id.email_edittext);
        mPassword = view.findViewById(R.id.password_edittext);

        mSignIn.setOnClickListener(this);
        mWarn1.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.signin_button:
                break;
            case R.id.warn1_textview:
                break;
        }

    }
}
