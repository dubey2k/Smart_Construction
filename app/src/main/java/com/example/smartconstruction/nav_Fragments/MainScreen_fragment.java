package com.example.smartconstruction.nav_Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartconstruction.R;
import com.google.android.material.textfield.TextInputEditText;

public class MainScreen_fragment extends Fragment {

    TextInputEditText Search;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.mainscreen_layout,container,false);
        Search=view.findViewById(R.id.Search);
        return view;
    }
}
