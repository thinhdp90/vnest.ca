package ai.kitt.snowboy.feature.result;

import android.net.Uri;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import ai.kitt.snowboy.App;
import ai.kitt.snowboy.R;
import ai.kitt.snowboy.activities.MainActivity;
import ai.kitt.snowboy.activities.ViewModel;
import ai.kitt.snowboy.service.TriggerOfflineService;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentResult extends Fragment {
    private final static String LOG_TAG = "Vnest Fragment Result";
    private RecyclerView mListResult;
    private TextView btnBack;
    private View iconBack;
    private AdapterResult adapter;
    private ViewModel viewModel;
    private Button btnVoice;
    private Boolean isStartingRecognitionProgressView = false;
    private RecognitionProgressView recognitionProgressView;
    private DataSource.Factory mediaSourceFactory;
    private PlayerView playerView;
    private ExoPlayer exoPlayer;
    private TrackSelector trackSelector;
    private ImageView btnClosePlayerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        intAction(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    public void initView(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        iconBack = view.findViewById(R.id.icon_back);
        mListResult = view.findViewById(R.id.mRecyclerView);
        btnVoice = view.findViewById(R.id.btnVoice);
        recognitionProgressView = view.findViewById(R.id.recognition_view);
        playerView = view.findViewById(R.id.playerView);
        btnClosePlayerView = view.findViewById(R.id.btnClosePlayerView);
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
                if(App.isActivated) {
                    startRecognition();
                }
            }
        });
        btnClosePlayerView.setOnClickListener(v -> {
            btnClosePlayerView.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            exoPlayer.stop();
            btnBack.setVisibility(View.VISIBLE);
            iconBack.setVisibility(View.VISIBLE);
            btnVoice.setVisibility(View.VISIBLE);
            recognitionProgressView.setVisibility(View.INVISIBLE);
        });
        btnBack.setOnClickListener(view1 -> {
            Objects.requireNonNull(getActivity()).onBackPressed();
        });
        iconBack.setOnClickListener(v -> {
            btnBack.performClick();
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

        viewModel.getLiveListPoi().observe(getViewLifecycleOwner(), pois -> {
            adapter.addItem(new ItemListResult(pois));
            mListResult.scrollToPosition(adapter.getItemCount() - 1);
        });
        viewModel.getLiveDataStartRecord().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean == null) return;
            if (aBoolean) {
                if(App.isActivated) {
                    startRecognition();
                }
            } else {
                finishRecognition();
            }
            viewModel.getLiveDataStartRecord().postValue(null);
        });
        mediaSourceFactory = new DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), "vnest"));

        viewModel.getLiveDataOpenVTV().observe(getViewLifecycleOwner(), this::playVideo);
        viewModel.getLiveDataRebindRecognitionsView().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean == null) return;
            if(aBoolean) {
              setUpRecognitionsUi();
            }
            viewModel.getLiveDataRebindRecognitionsView().postValue(null);
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
        Log.e("sdfsddddddddddd", SpeechRecognizer.isRecognitionAvailable(requireContext()) +"" );

    }

    public void startRecognition() {
        Log.d(LOG_TAG, "start listener....");
        TriggerOfflineService.stopService(requireContext());
        isStartingRecognitionProgressView = true;
        btnVoice.setVisibility(View.GONE);
        setMarginListResult(120);
        recognitionProgressView.play();
        recognitionProgressView.setVisibility(View.VISIBLE);
        getMainActivity().getSpeechRecognizerManager().startListening();
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

    private void setMarginListResult(int topMargin) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mListResult.getLayoutParams();
        layoutParams.topMargin = (int) (getResources().getDisplayMetrics().scaledDensity * topMargin);
        mListResult.scrollToPosition(adapter.getItemCount() - 1);
        mListResult.setLayoutParams(layoutParams);
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

//    private void initializePlayer() {
//
//        playerView.requestFocus();
//
//        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
//
//        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
////        lastSeenTrackGroupArray = null;
//
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext(), trackSelector);
//
//        playerView.setPlayer(exoPlayer);
//        exoPlayer.setPlayWhenReady(true);
//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(mediaSourceFactory)
//                .createMediaSource(Uri.parse("https://www.youtube.com/watch?v=xg4S67ZvsRs"));
//        exoPlayer.prepare(mediaSource);
//
//    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    requireContext(),
                    new DefaultRenderersFactory(requireContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
            playerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(true);
//            exoPlayer.seekTo(currentWindow, playbackPosition);
        }
    }

    private void playVideo(String url) {
        if (url == null) return;
        if(url.equalsIgnoreCase("-1")) {
            btnClosePlayerView.performClick();
            return;
        }
        viewModel.getLiveDataOpenVTV().postValue(null);
        finishRecognition();
        playerView.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.INVISIBLE);
        iconBack.setVisibility(View.INVISIBLE);
        btnVoice.setVisibility(View.GONE);
        recognitionProgressView.setVisibility(View.INVISIBLE);
        btnClosePlayerView.setVisibility(View.VISIBLE);
        MediaSource mediaSource = buildMediaSource(Uri.parse(url));
        exoPlayer.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(@NonNull Uri uri) {
        String userAgent = "exoplayer-vnest";
        if (Objects.requireNonNull(uri.getLastPathSegment()).contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else if (uri.getLastPathSegment().contains("m3u8")) {
            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {
            DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
                    new DefaultHttpDataSourceFactory(userAgent));
            DefaultHttpDataSourceFactory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
            return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri);
        }
    }


}
