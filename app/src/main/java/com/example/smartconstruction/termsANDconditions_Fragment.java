package com.example.smartconstruction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class termsANDconditions_Fragment extends Fragment {
    TextView terms;
    Button Agree;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_terms, container, false);
        terms = view.findViewById(R.id.termsandconditonScreen);
        Agree = view.findViewById(R.id.Agree_btn);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alt = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.terms_and_condition, null);
                alt.setView(view);
                TextView title = view.findViewById(R.id.titleTermsAndConditions);
                TextView description = view.findViewById(R.id.descriptionTermsAndConditions);
                title.setText("Terms And Conditions\nfor CUSTOMERS");
                description.setText(R.string.terms_and_conditions_customer);
                final AlertDialog alertDialog = alt.create();
                alertDialog.show();
            }
        });

        Agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

}
