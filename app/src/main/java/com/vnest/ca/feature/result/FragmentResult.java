package com.vnest.ca.feature.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnest.ca.R;

public class FragmentResult extends Fragment {
    private RecyclerView mListResult;
    private TextView btnBack;
    private AdapterResult adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        initView(view);
        intAction(view);
        return view;
    }

    public void initView(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        mListResult = view.findViewById(R.id.mRecyclerView);
    }

    public void intAction(View view) {
        adapter = new AdapterResult();
        mListResult.setAdapter(adapter);
        mListResult.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
