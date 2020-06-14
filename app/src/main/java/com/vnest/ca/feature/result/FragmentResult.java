package com.vnest.ca.feature.result;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.vnest.ca.R;
import com.vnest.ca.activities.MainActivity;
import com.vnest.ca.activities.ViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentResult extends Fragment {
    private final static String LOG_TAG = "Vnest Fragment Result";
    private RecyclerView mListResult;
    private TextView btnBack;
    private AdapterResult adapter;
    private ViewModel viewModel;
    private Button btnVoice;
    private Boolean isStartingRecognitionProgressView = false;
    private RecognitionProgressView recognitionProgressView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ViewModel.class);
    }

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
        btnVoice = view.findViewById(R.id.btnVoice);
        recognitionProgressView = view.findViewById(R.id.recognition_view);
        initRecognitionProgressView();
    }

    public void intAction(View view) {
        setUpRecognitionsUi();
        adapter = new AdapterResult();
        mListResult.setAdapter(adapter);
        mListResult.setLayoutManager(new LinearLayoutManager(getContext()));
        btnVoice.setOnClickListener(view1 -> {
            if (isStartingRecognitionProgressView) {
                finishRecognition();
            } else {
                startRecognition();
            }
        });
        btnBack.setOnClickListener(view1 -> {
            Objects.requireNonNull(getActivity()).onBackPressed();
        });

        viewModel.getListMessLiveData().observe(getViewLifecycleOwner(), list -> {
            ArrayList<ResultItem> listResultItem = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                listResultItem.add(new ItemAssistant(list.get(i).getMessage(), list.get(i).isSender()));
            }
            adapter.setListItem(listResultItem);
            mListResult.scrollToPosition(adapter.getItemCount() - 1);
        });
        viewModel.getLiveDataProcessText().observe(getViewLifecycleOwner(), message -> {
            adapter.addItem(new ItemAssistant(message.getMessage(), message.isSender()));
            mListResult.scrollToPosition(adapter.getItemCount() - 1);
            finishRecognition();
        });
        viewModel.getLiveDataTextToSpeech().observe(getViewLifecycleOwner(), text -> {
            Log.e("Text to speech", text);
            ((MainActivity) Objects.requireNonNull(getActivity())).textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        });
        viewModel.getLiveListPoi().observe(getViewLifecycleOwner(), pois -> {
            adapter.addItem(new ItemListResult(pois));
            mListResult.scrollToPosition(adapter.getItemCount() - 1);
        });
        viewModel.getLiveDataStartRecord().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean == null) return;
            if (aBoolean) {
                startRecognition();
            } else {
                finishRecognition();
            }
            viewModel.getLiveDataStartRecord().postValue(null);
        });
    }

    private void setUpRecognitionsUi() {
        recognitionProgressView.setOnClickListener(view1 -> {
            finishRecognition();
        });
        recognitionProgressView.setSpeechRecognizer(getMainActivity().getSpeechRecognizerManager().getSpeechRecognizer());
        recognitionProgressView.setRecognitionListener(getMainActivity().getSpeechRecognizerManager().getSpeechListener());
    }

    private void initRecognitionProgressView() {
        int[] colors = {
                ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color1),
                ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color2),
                ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color3),
                ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color4),
                ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color5)
        };

        int[] heights = {60, 76, 58, 80, 55};

        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.setCircleRadiusInDp(6); // kich thuoc cham tron
        recognitionProgressView.setSpacingInDp(2); // khoang cach giua cac cham tron
        recognitionProgressView.setIdleStateAmplitudeInDp(8); // bien do dao dong cua cham tron
        recognitionProgressView.setRotationRadiusInDp(0); // kich thuoc vong quay cua cham tron
        recognitionProgressView.play();

    }

    public void startRecognition() {
        Log.d(LOG_TAG, "start listener....");
        isStartingRecognitionProgressView = true;
        recognitionProgressView.play();
        recognitionProgressView.setVisibility(View.VISIBLE);
        getMainActivity().getSpeechRecognizerManager().startListening();
    }

    /**
     * Finish Speech Recognition
     */
    public void finishRecognition() {
        Log.d(LOG_TAG, "stop listener....");
        isStartingRecognitionProgressView = false;
        recognitionProgressView.stop();
        recognitionProgressView.setVisibility(View.INVISIBLE);
        getMainActivity().getSpeechRecognizerManager().stopListening();
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

}
