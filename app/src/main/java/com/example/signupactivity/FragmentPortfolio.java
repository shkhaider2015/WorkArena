package com.example.signupactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentPortfolio extends Fragment {

    View mPortfolioHeader, mPortfolioDescription, mPortfolioPersonalInfo, mPortfolioFeedback;
    TextView hFullName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portfolio_fragment, container, false);

        mPortfolioHeader = view.findViewById(R.id.portfolio_header);
        mPortfolioDescription = view.findViewById(R.id.portfolio_description);
        mPortfolioPersonalInfo = view.findViewById(R.id.portfolio_personal_info);
        mPortfolioFeedback = view.findViewById(R.id.portfolio_feedback);

        hFullName = mPortfolioHeader.findViewById(R.id.portfolio_header_full_name);
        hFullName.setText("Shakeel Haider");




        return view;
    }
}
