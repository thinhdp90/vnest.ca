package com.vnest.ca.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vnest.ca.R;
import com.vnest.ca.activities.MainActivity;
import com.vnest.ca.adapters.DefaultItemAdapter;

import java.util.Objects;

public class FragmentHome extends Fragment {
    private Button btnListener;
    private RecyclerView mRecyclerView;
    private DefaultItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initAction(view);
        return view;
    }

    private void initView(View view) {
        btnListener = view.findViewById(R.id.btnVoice);
        mRecyclerView = view.findViewById(R.id.recyclerview_def_item);
    }

    private void initAction(View view) {
        btnListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start to listener fragment or hide/show recycler view
            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
    }
}
