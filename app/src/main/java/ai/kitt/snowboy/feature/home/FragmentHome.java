package ai.kitt.snowboy.feature.home;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.RecyclerView;


import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;

import ai.kitt.snowboy.activities.MainActivity;
import ai.kitt.snowboy.activities.ViewModel;
import ai.kitt.snowboy.entity.Message;
import ai.kitt.snowboy.feature.result.FragmentResult;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ai.kitt.snowboy.R;
import ai.kitt.snowboy.util.Utils;

public class FragmentHome extends Fragment implements SpeechDelegate, Speech.stopDueToDelay {
    private static final String LOG_TAG = "VNest";
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
    private AdapterHomeItemDefault adapter;
    private View backIcon;
    private TextView btnBack;
    private TextView assistantText;
    private RecognitionProgressView recognitionProgressView;
    private Boolean isStartingRecognitionProgressView = false;
    private ViewModel viewModel;
    private TextToSpeech textToSpeech;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ViewModel.class);
    }

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
        btnBack = view.findViewById(R.id.btn_back);
        backIcon = view.findViewById(R.id.icon_back);
        assistantText = view.findViewById(R.id.text_assistant);
        recognitionProgressView = view.findViewById(R.id.recognition_view);
        initRecognitionProgressView();
    }

    private void initAction(View view) {
        viewModel.getMessage();
        if (textToSpeech != null) {
            textToSpeech = new TextToSpeech(getActivity(), status -> {
                Log.e("Status", status + "");
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            });
        }

        btnListener.setOnClickListener(view1 -> {
            startResultFragment(true);
//            showViewOnBtnBackClick(true);
//            if (isStartingRecognitionProgressView) {
//                finishRecognition();
//            } else {
//                startRecognition();
//            }
        });
//        recognitionProgressView.setOnClickListener(view1 -> {
//            finishRecognition();
//            getMainActivity().speechRecognizerOnline.stopListening();
//        });
        btnBack.setOnClickListener(view1 -> {
            showViewOnBtnBackClick(false);
            finishRecognition();

        });

//        recognitionProgressView.setSpeechRecognizer(getMainActivity().speechRecognizerOnline);
        viewModel.getListMessLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list.size() > 1) {
                startResultFragment();
            } else {
                viewModel.saveMessage(new Message("Chào bạn, tôi có thể giúp gì cho bạn", false, Calendar.getInstance().getTimeInMillis()));
            }
        });
    }

    public void startResultFragment() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new FragmentResult(), FragmentResult.class.getName())
                .addToBackStack(MainActivity.class.getName())
                .commit();
    }

    public void startResultFragment(Boolean startRecord) {
        startResultFragment();
        viewModel.getLiveDataStartRecord().postValue(startRecord);
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

//        recognitionProgressView.setColors(colors);
//        recognitionProgressView.setBarMaxHeightsInDp(heights);
//        recognitionProgressView.setCircleRadiusInDp(6); // kich thuoc cham tron
//        recognitionProgressView.setSpacingInDp(2); // khoang cach giua cac cham tron
//        recognitionProgressView.setIdleStateAmplitudeInDp(8); // bien do dao dong cua cham tron
//        recognitionProgressView.setRotationRadiusInDp(40); // kich thuoc vong quay cua cham tron
//        recognitionProgressView.play();

    }

    private void showViewOnBtnBackClick(Boolean shouldShow) {
        if (shouldShow) {
            backIcon.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
        } else {
            backIcon.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
        }
    }

    public void setAssistantProcessingText(Message message) {
        assistantText.setText(message.getMessage());
        viewModel.saveMessage(message);
    }

    /**
     * Finish Speech Recognition
     */
    public void finishRecognition() {
        Log.d(LOG_TAG, "finishRecognition - stop listener....");
        isStartingRecognitionProgressView = false;
        recognitionProgressView.stop();
        recognitionProgressView.play();
        recognitionProgressView.setVisibility(View.GONE);
        getMainActivity().getSpeechRecognizerOnline().stopListening();

        startTrigerOnline();
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    private String[] listHotWord = new String[]{"xinchaoalex", "okealex", "xinchaoem",
            "okealice", "xinchaoalice", "alexa", "Alaska"};
    public static SpeechDelegate delegate;

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getMainActivity().getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }

            if (Speech.getInstance().isListening()) {
                muteBeepSoundOfRecorder();
                Speech.getInstance().stopListening();
            } else {
                Speech.getInstance().stopTextToSpeech();
                try {
                    Speech.getInstance().startListening(null, this);
                } catch (SpeechRecognitionNotAvailable speechRecognitionNotAvailable) {
                    speechRecognitionNotAvailable.printStackTrace();
                } catch (GoogleVoiceTypingDisabledException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        muteBeepSoundOfRecorder();
    }

    @Override
    public void onStartOfSpeech() {

    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d(LOG_TAG, "onSpeechPartialResults: " + partial);
        }
    }

    @Override
    public void onSpeechResult(String result) {
        Log.d("LOG_TAG", "onSpeechResult:" + result);
        if (!TextUtils.isEmpty(result)) {
            result = Utils.formatString(result).replace(" ", "").toLowerCase().trim();
            for (String hw : listHotWord) {
                if (result.contains(hw)) {
                    viewModel.getLiveDataStartRecord().postValue(true);
                    Speech.getInstance().shutdown();
                }
            }
        }
    }


    private void muteBeepSoundOfRecorder() {
        AudioManager amanager = (AudioManager) getMainActivity().getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                amanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            } else {
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    private void startTrigerOnline() {
        Speech.init(getMainActivity());
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
//            muteBeepSoundOfRecorder();
        } else {
            try {
                Speech.getInstance().stopTextToSpeech();
                Speech.getInstance().startListening(null, this);
            } catch (SpeechRecognitionNotAvailable exc) {
//                showSpeechNotSupportedDialog();

            } catch (GoogleVoiceTypingDisabledException exc) {
//                showEnableGoogleVoiceTyping();
            }
            muteBeepSoundOfRecorder();
        }
    }
}
