package com.vnest.ca.feature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vnest.ca.R;

public class FragmentListener extends Fragment {
    private Button btnBack;
    private Button btnVoice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listener, container, false);
        initView(view);
        initAction(view);
        return view;
    }

    private void initView(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnVoice = view.findViewById(R.id.btnVoice);
    }

    private void initAction(View view) {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start voice record
            }
        });
    }
}
