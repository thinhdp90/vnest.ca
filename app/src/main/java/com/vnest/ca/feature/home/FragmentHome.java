package com.vnest.ca.feature.home;

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

import java.util.Objects;

public class FragmentHome extends Fragment {
    private final String[] defItems = {"Open \"Bang Kieu\" Playlist",
            "Open \"VOV giao thong\"",
            "\"Navigation\" to nearest ATM",
            "Open \"Bich Phuong\" via \"Zing MP3\"",
            "Open Google Maps",
            "\"Navigation\" to 22 Ngo 151 Ton That Tung Dong Da Ha Noi",
            "Open \"Youtube\"",
            "\"Navigation\" to nearest VPBank",
            "See more..."};

    private Button btnListener;
    private RecyclerView mRecyclerView;
    private AdapterHomeItemDefault adapter;

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
        adapter = new AdapterHomeItemDefault(getContext(), ((MainActivity) Objects.requireNonNull(getActivity())).getTextToSpeech(), text -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).processing_text(text);
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
    }
}
