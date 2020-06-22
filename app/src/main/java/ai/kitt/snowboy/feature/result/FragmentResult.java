package ai.kitt.snowboy.feature.result;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;


import ai.kitt.snowboy.activities.MainActivity;
import ai.kitt.snowboy.activities.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ai.kitt.snowboy.R;
import ai.kitt.snowboy.triggerword.TriggerOnline;
import ai.kitt.snowboy.triggerword.TriggerOnlineInActivity;
import ai.kitt.snowboy.util.Utils;


public class FragmentResult extends Fragment {
    private final static String LOG_TAG = "Vnest Fragment Result";
    private RecyclerView mListResult;
    private TextView btnBack;
    private AdapterResult adapter;
    private ViewModel viewModel;
    private Button btnVoice;
    private Boolean isStartingRecognitionProgressView = false;
    private RecognitionProgressView recognitionProgressView;
    private Intent intent;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ViewModel.class);
        intent = new Intent(getActivity(), TriggerOnlineInActivity.class);
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
        recognitionProgressView.setOnClickListener(v -> {
            finishRecognitionAndListen();
        });
        initRecognitionProgressView();
    }

    public void intAction(View view) {
        setUpRecognitionsUi();
        adapter = new AdapterResult();
        mListResult.setAdapter(adapter);
        mListResult.setLayoutManager(new LinearLayoutManager(getContext()));
        btnVoice.setOnClickListener(view1 -> {
            if (isStartingRecognitionProgressView) {
                finishRecognitionAndListen();
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
            viewModel.saveMessage(message);
            adapter.addItem(new ItemAssistant(message.getMessage(), message.isSender()));
            mListResult.scrollToPosition(adapter.getItemCount() - 1);
            finishRecognition();
        });
        viewModel.getLiveDataTextToSpeech().observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                Log.e("Text to speech", text);
                ((MainActivity) Objects.requireNonNull(getActivity())).speak(text, true);
                viewModel.getLiveDataTextToSpeech().postValue(null);
            }

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
                finishRecognitionAndListen();
            }
            viewModel.getLiveDataStartRecord().postValue(null);
        });
    }

    private void setUpRecognitionsUi() {
        recognitionProgressView.setOnClickListener(view1 -> {
            finishRecognitionAndListen();
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
        btnVoice.setVisibility(View.GONE);
        setMarginListResult(120);
        recognitionProgressView.play();
        getMainActivity().getSpeechRecognizerManager().startListening();
        recognitionProgressView.setVisibility(View.VISIBLE);
        try {
            getActivity().unbindService(getMainActivity().getServiceConnection());
            getActivity().stopService(intent);
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);

        }
    }

    /**
     * Finish Speech Recognition
     */
    public void finishRecognition() {
        Log.d(LOG_TAG, "stop listener....");
        btnVoice.setVisibility(View.VISIBLE);
        setMarginListResult(37);
        isStartingRecognitionProgressView = false;
        recognitionProgressView.stop();
        recognitionProgressView.setVisibility(View.INVISIBLE);
        getMainActivity().getSpeechRecognizerManager().stopListening();
    }

    public void finishRecognitionAndListen() {
        Log.d(LOG_TAG, "stop listener and listen trigger....");
        btnVoice.setVisibility(View.VISIBLE);
        setMarginListResult(37);
        isStartingRecognitionProgressView = false;
        recognitionProgressView.stop();
        recognitionProgressView.setVisibility(View.INVISIBLE);
        getMainActivity().getSpeechRecognizerManager().destroy();

        try {
            Speech.getInstance().shutdown();
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);

        }

        getActivity().startService(intent);
        getActivity().bindService(intent, getMainActivity().getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    private void setMarginListResult(int topMargin) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mListResult.getLayoutParams();
        layoutParams.topMargin = (int) (getResources().getDisplayMetrics().scaledDensity * topMargin);
        mListResult.scrollToPosition(adapter.getItemCount() - 1);
        mListResult.setLayoutParams(layoutParams);
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

}
