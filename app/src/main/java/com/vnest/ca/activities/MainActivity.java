package com.vnest.ca.activities;

import ai.api.model.AIContext;
import ai.api.model.AIOutputContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kwabenaberko.openweathermaplib.constants.Lang;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.vnest.ca.R;
import com.vnest.ca.adapters.DefaultAssistantAdapter;
import com.vnest.ca.feature.home.AdapterHomeItemDefault;
import com.vnest.ca.adapters.ItemNavigationAdapter;
import com.vnest.ca.adapters.MessageListAdapter;
import com.vnest.ca.entity.Audio;
import com.vnest.ca.entity.Message;
import com.vnest.ca.entity.MyAIContext;
import com.vnest.ca.entity.Poi;
import com.vnest.ca.entity.Youtube;
import com.vnest.ca.feature.home.FragmentHome;
import com.vnest.ca.triggerword.Trigger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String LOG_TAG = "VNest";
    private static final int UPDATE_AFTER_PROCESS_TEXT = 4;
    private static final int RESTART_VOICE_RECOGNITION = 1;

    private String[] permissions = {Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SET_ALARM};
    private final String[] defItems = {"Open \"Bang Kieu\" Playlist",
            "Open \"VOV giao thong\"",
            "\"Navigation\" to nearest ATM",
            "Open \"Bich Phuong\" via \"Zing MP3\"",
            "Open Google Maps",
            "\"Navigation\" to 22 Ngo 151 Ton That Tung Dong Da Ha Noi",
            "Open \"Youtube\"",
            "\"Navigation\" to nearest VPBank",
            "See more..."};

    private FrameLayout fragmentContainer;
    private RecyclerView mMessageRecycler;
    private List<Message> messageList;
    private MessageListAdapter mMessageAdapter;
    private ImageButton btnListen;
    private FrameLayout layout_speech;
    private LinearLayout layoutSpeechAndKeyboard;

    private RecognitionProgressView recognitionProgressView;

    private TextToSpeech textToSpeech;

    private SpeechRecognizer speechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private Intent mBackgroundSpeechRecognizerIntent;

    private AIService aiService;
    private static boolean isExcecuteText = false;

    private LocationManager locationManager;
    private double latitude, longitude;
    private OpenWeatherMapHelper weather;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private String deviceId;
    private boolean notchangesessionid = false;
    private String currentSessionId;

    private List<AIOutputContext> contexts;
    private Handler mHandler;
    private Boolean isShouldProcessText;
    private RecyclerView mRecyclerViewDefaultAssistant;
    private RecyclerView mRecyclerViewDefaultMainItem;
    private RecyclerView mRecyclerViewAfterProcessItem;
    private View mCollapseView;
    private TextView processTextView;
    private Boolean isStartRecognizer;

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public void setTextToSpeech(TextToSpeech textToSpeech) {
        this.textToSpeech = textToSpeech;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermission()) {
            initIfPermissionGranted();
        } else {
            requestPermission();
        }


    }

    private void initIfPermissionGranted() {
        init();
        initAction();

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

//        startRecognition();
        speechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    private void init() {
        initView();
        deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // Get phone's location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        // setup UI Message
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);

        layout_speech = findViewById(R.id.layout_speech);
        layoutSpeechAndKeyboard = findViewById(R.id.layout_speech_and_keyboard);
        btnListen = findViewById(R.id.btnListen);
        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        messageList = new ArrayList<>();

        mMessageAdapter = new MessageListAdapter(this, messageList);

        if (mMessageRecycler != null) {
            mMessageRecycler.setAdapter(mMessageAdapter);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

        }

        messageList.add(new Message("Chào bạn, tôi có thể giúp gì cho bạn!", false, System.currentTimeMillis()));

        readCsvMessage();
        if (recognitionProgressView != null) {
            setUiRecognition(this.getApplicationContext());
        }
        final AIConfiguration config = new AIConfiguration("73cf2510f55c425eb5f5d8bb20d6d3e7",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);


    }

    private void initView() {
        fragmentContainer = findViewById(R.id.fragment_container);
        mRecyclerViewDefaultAssistant = findViewById(R.id.recycler_view_def_assistant);
        mRecyclerViewDefaultMainItem = findViewById(R.id.recyclerview_def_item);
        mRecyclerViewAfterProcessItem = findViewById(R.id.recyclerview_item_after_process);
        processTextView = findViewById(R.id.text_process);
        mCollapseView = findViewById(R.id.view_collapse);
        if (mRecyclerViewDefaultMainItem != null) {
            mRecyclerViewDefaultMainItem.setAdapter(new AdapterHomeItemDefault(this, textToSpeech, new AdapterHomeItemDefault.OnProcessingText() {
                @Override
                public void process(String text) {
                    processing_text(text);
                }
            }));
            mRecyclerViewDefaultMainItem.setLayoutManager(new GridLayoutManager(this, 3));
        }
        if (mRecyclerViewDefaultAssistant != null) {
            mRecyclerViewDefaultAssistant.setAdapter(new DefaultAssistantAdapter(new DefaultAssistantAdapter.OnItemClickListener() {
                @Override
                public void onClick(String text) {
                    try {
                        textToSpeech.speak("Bạn chưa thể sử dụng " + text, TextToSpeech.QUEUE_FLUSH, null);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                }
            }));
            mRecyclerViewDefaultAssistant.setLayoutManager(new LinearLayoutManager(this));
            if (mRecyclerViewAfterProcessItem != null) {
                mRecyclerViewAfterProcessItem.setVisibility(View.GONE);

            }

        }
        if (fragmentContainer != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentHome())
                    .commit();
        }

    }

    private void setProcessText(String processText) {
        if (processTextView != null) {
            processTextView.setText(processText);
        }
    }

    private void initAction() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        if (btnListen != null) {
            btnListen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "onClick listener....");
                    if (!getResources().getBoolean(R.bool.isTablet)) {
                        isShouldProcessText = true;
                        speechRecognizer.stopListening();
                        startRecognition();
                    } else {
                        if (isStartRecognizer == null || !isStartRecognizer) {
                            isShouldProcessText = true;
                            speechRecognizer.stopListening();
                            startRecognition();
                        } else {
                            finishRecognition();
                        }
                    }

                }
            });

        }

        if (mCollapseView != null) {
            mCollapseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewDefaultAssistant.getVisibility() == View.GONE) {
                        mRecyclerViewDefaultAssistant.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerViewDefaultAssistant.setVisibility(View.GONE);
                    }
                }
            });
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case RESTART_VOICE_RECOGNITION:
                        startRecognition();
                        break;
                    case 2:
//                        finishRecognition();
//                        speechRecognizer.stopListening();
//
//                        //Start service
//                        Intent intent = new Intent(MainActivity.this, Trigger.class);
//                        startService(intent);
//                        startRecognition();
                        break;
                    case 3:
                        startDetectOpenVoiceRecognizer();
                        break;
                    case UPDATE_AFTER_PROCESS_TEXT:
                        ItemNavigationAdapter adapter = (ItemNavigationAdapter) msg.obj;
                        mRecyclerViewAfterProcessItem.setAdapter(adapter);
                        mRecyclerViewAfterProcessItem.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        mRecyclerViewAfterProcessItem.setVisibility(View.VISIBLE);
                        mRecyclerViewDefaultMainItem.setVisibility(View.INVISIBLE);
                    default:
                        break;
                }
            }
        };
    }

    private void setUiRecognition(Context context) {
        //setup weather
        weather = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));
        weather.setUnits(Units.METRIC);
        weather.setLang(Lang.VIETNAMESE);

        // setup Speech Recognition
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onPartialResults(Bundle partialResults) {
                super.onPartialResults(partialResults);
                Log.e("result", "onPartialResults");
            }

            @Override
            public void onEndOfSpeech() {
                super.onEndOfSpeech();
                Log.e("End Speed", "Speed end");
                Log.e("isShouldProcessText", isShouldProcessText + "");
                if (isShouldProcessText == null || !isShouldProcessText) {
                    android.os.Message message = mHandler.obtainMessage(2);
                    message.sendToTarget();
                    return;
                }
                Log.e("isExcecuteText", isExcecuteText + "");
                if (!isExcecuteText) {
                    speechRecognizer.startListening(mSpeechRecognizerIntent);
                }
            }

            @Override
            public void onResults(Bundle results) {
                Log.e("End result", "end");
                if (isExcecuteText) {
                    return;
                }
                finishRecognition();
                speechRecognizer.stopListening();
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String text = matches.get(0);
                Log.d(LOG_TAG, "onResults: " + text);
                if (text.toLowerCase().contains("ok") && (isShouldProcessText == null || !isShouldProcessText)) {
                    isShouldProcessText = true;
                    speechRecognizer.stopListening();
                    textToSpeech.speak("Mời bạn nói", TextToSpeech.QUEUE_FLUSH, null);
                    android.os.Message message = mHandler.obtainMessage(RESTART_VOICE_RECOGNITION);
                    message.sendToTarget();
//                    startRecognition();
                    return;
                }
                if (isShouldProcessText == null || !isShouldProcessText) {
                    Log.e(LOG_TAG, "isShouldProcessText" + isShouldProcessText);
                    speechRecognizer.startListening(mSpeechRecognizerIntent);
                    return;
                }
                Log.e(LOG_TAG, "Procees text" + isShouldProcessText);
                if (isShouldProcessText != null && isShouldProcessText) {
                    Log.e(LOG_TAG, "Procees text");
                    isExcecuteText = true;
                    sendMessage(text, true);
                    processing_text(text);
                } else {
                    Log.e(LOG_TAG, "Procees text false");
                    speechRecognizer.startListening(mSpeechRecognizerIntent);
                }

            }
        });

        recognitionProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishRecognition();
                speechRecognizer.stopListening();

            }
        });

        int[] colors = {
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this, R.color.color4),
                ContextCompat.getColor(this, R.color.color5)
        };

        int[] heights = {60, 76, 58, 80, 55};


        recognitionProgressView.setColors(colors);
        recognitionProgressView.setBarMaxHeightsInDp(heights);
        recognitionProgressView.setCircleRadiusInDp(6); // kich thuoc cham tron
        recognitionProgressView.setSpacingInDp(2); // khoang cach giua cac cham tron
        recognitionProgressView.setIdleStateAmplitudeInDp(8); // bien do dao dong cua cham tron
        recognitionProgressView.setRotationRadiusInDp(40); // kich thuoc vong quay cua cham tron
        recognitionProgressView.play();


    }

    /**
     * Start Speech Recognition
     */
    private void startRecognition() {
        Log.d(LOG_TAG, "start listener....");
        isStartRecognizer = true;
        if (!getResources().getBoolean(R.bool.isTablet)) {
            btnListen.setVisibility(View.GONE);
        }
        if (layoutSpeechAndKeyboard != null) {
            layoutSpeechAndKeyboard.setVisibility(View.GONE);
        }
        recognitionProgressView.play();
        recognitionProgressView.setVisibility(View.VISIBLE);
        speechRecognizer.startListening(mSpeechRecognizerIntent);
        isExcecuteText = false;
    }

    /**
     * Finish Speech Recognition
     */
    private void finishRecognition() {
        isStartRecognizer = false;
        if (btnListen != null) {
            btnListen.setVisibility(View.VISIBLE);
        }
        if (layoutSpeechAndKeyboard != null) {
            layoutSpeechAndKeyboard.setVisibility(View.VISIBLE);

        }
        if (recognitionProgressView != null) {
            recognitionProgressView.stop();
            recognitionProgressView.play();
            recognitionProgressView.setVisibility(View.GONE);

        }
    }

    public void processing_text(final String text) {
        Log.d(LOG_TAG, "================= processing_text: " + text);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AIRequest aiRequest = new AIRequest(text);
                    if (!notchangesessionid || currentSessionId == null) {
                        currentSessionId = deviceId + "#" + latitude + "-" + longitude;
                    }
                    aiRequest.setSessionId(currentSessionId);
                    if (contexts != null) {
                        List<AIContext> rqContexts = new ArrayList<>();
                        for (AIOutputContext oc : contexts) {
                            rqContexts.add(new MyAIContext(oc));
                        }
                        aiRequest.setContexts(rqContexts);
                    }

                    Log.d(LOG_TAG, "===== aiRequest:" + gson.toJson(aiRequest));
                    try {
                        AIResponse aiRes = aiService.textRequest(aiRequest);
                        Log.d(LOG_TAG, gson.toJson(aiRes));
                        String action = aiRes.getResult().getAction();
                        Log.d(LOG_TAG, "===== action:" + action);
                        contexts = aiRes.getResult().getContexts();
                        String code = null;
                        try {
                            notchangesessionid = aiRes.getResult().getFulfillment().getData().get("notchangesessionid").getAsBoolean();
                            code = aiRes.getResult().getFulfillment().getData().get("code").toString().replace("\"", "");
                            Log.d(LOG_TAG, "===== notchangesessionid: " + notchangesessionid);
                        } catch (Exception e) {

                        }

                        Log.e("Actions", action);
//                        String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
                        switch (action) {
//                            case action.contains("OpenBank"):
//                                break;
                            case "OpenBankPlaceUnknownSpeech":
                            case "OpenDrinkPlaceUnknownSpeech":
                            case "OpenEatPlaceUnknownSpeech":
                            case "OpenBankPlaceWhatever":
                                search_unknown(text, aiRes);
                                break;
                            case "OpenBankPlace":
//                                break;
//                                break;
                            case "OpenDrinkPlace":
                            case "OpenDrinkPlaceUnknown":
                            case "OpenEatPlace":
                            case "OpenEatPlaceUnknown":
                            case "OpenEatPlaceWhatever":
                            case "OpenBankPlaceUnknown":
                                search_bank(action, aiRes);
                                break;
                            case "input.unknown":
                                String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
                                Log.d(LOG_TAG, "===== textSpeech:" + textSpeech);
                                textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null);
                                sendMessage(textSpeech, false);
                                break;
                            case "Mp3":
                                if (code != null) {
                                    Log.d(LOG_TAG, "======= code:" + code);
                                    if (code.equals("1")) {
                                        Audio audio = gson.fromJson(
                                                aiRes.getResult().getFulfillment().getData().get("audios").getAsJsonArray().get(0).toString(), Audio.class);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                                        https://zingmp3.vn/album/Nhung-Bai-Hat-Hay-Nhat-Cua-Bang-Kieu-Bang-Kieu/ZWZ9DAEI.html
                                        intent.setData(Uri.parse(audio.getLink()));
                                        intent.setPackage("com.zing.mp3");
                                        startActivity(intent);
                                        sendMessage(audio.getAlias(), false);
                                        processing_text(audio.getAlias());
                                    } else {
                                        textSpeech = aiRes.getResult().getFulfillment().getSpeech();
                                        Log.d(LOG_TAG, "===== textSpeech:" + textSpeech);
                                        textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null);
                                        sendMessage(textSpeech, false);
                                        processing_text(textSpeech);

                                    }
                                } else {
                                    textSpeech = aiRes.getResult().getFulfillment().getSpeech();
                                    Log.d(LOG_TAG, "===== textSpeech:" + textSpeech);
                                    textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null);
                                    sendMessage(textSpeech, false);
                                    processing_text(textSpeech);
                                }

                                break;
                            case "Youtube":
                                Youtube video = gson.fromJson(
                                        aiRes.getResult().getFulfillment().getData().get("videos").getAsJsonArray().get(0).toString(), Youtube.class);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(video.getHref()));
                                intent.setPackage("com.google.android.youtube");
                                startActivity(intent);
                                sendMessage(video.getHref(), false);
                                break;
                            default:
                                if (text.toLowerCase().contains("thời tiết")) {
                                    weather();
                                } else if (text.toLowerCase().contains("tìm")) {
                                    search(text);
                                } else {
                                    textToSpeech.speak("Hiện không tìm thấy thông tin", TextToSpeech.QUEUE_FLUSH, null);
                                }
                        }
//                        android.os.Message message = mHandler.obtainMessage(2);
//                        message.sendToTarget();

                    } catch (AIServiceException e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isExcecuteText = false;
                }
            }
        });
        thread.start();
    }

    private void startDetectOpenVoiceRecognizer() {
        isShouldProcessText = false;
//        isExcecuteText = true;
        speechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    private void search_unknown(String text, AIResponse aiRes) throws InterruptedException {
        String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
        textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null);
        sendMessage(textSpeech, false);
        Thread.sleep(3000);
        android.os.Message message = mHandler.obtainMessage(RESTART_VOICE_RECOGNITION);
        message.sendToTarget();
    }

    private void search_bank(String key, AIResponse aiResponse) {
        Log.e("Data", gson.toJson(aiResponse));

        try {
            JsonArray dataResponse = aiResponse.getResult().getFulfillment().getData().get("pois").getAsJsonArray();
            if (dataResponse.isJsonNull() || dataResponse.size() < 1) {
                Log.e("Data", "null");
                textToSpeech.speak("Không tìm thấy kết quả bạn mong muốn! Vui lòng thử lại", TextToSpeech.QUEUE_FLUSH, null);
                sendMessage("Không tìm thấy kết quả bạn mong muốn! Vui lòng thử lại", false);
            } else if (dataResponse.size() >= 1 && getResources().getBoolean(R.bool.isTablet)) {
                textToSpeech.speak("Vui lòng chọn nơi bạn muốn đến", TextToSpeech.QUEUE_FLUSH, null);
                setProcessText("Vui lòng chọn nơi bạn muốn đến");
                final ArrayList<Poi> poiArrayList = new ArrayList<>();
                dataResponse.getAsJsonArray().forEach(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement jsonElement) {
                        poiArrayList.add(gson.fromJson(jsonElement, Poi.class));
                    }
                });
                ItemNavigationAdapter adapter = new ItemNavigationAdapter(new ItemNavigationAdapter.ItemCLickListener() {
                    @Override
                    public void onItemClick(Poi poi) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
                adapter.setData(poiArrayList);
                android.os.Message message = mHandler.obtainMessage(UPDATE_AFTER_PROCESS_TEXT);
                message.obj = adapter;
                message.sendToTarget();


            } else {
                Poi poi = gson.fromJson(dataResponse.get(0), Poi.class);
                Log.e("Gps", "geo:" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
//            textToSpeech.speak("Không tìm thấy kết quả bạn mong muốn! Vui lòng thử lại", TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    private void sendMessage(String text, boolean isUser) {

        messageList.add(new Message(text, isUser, System.currentTimeMillis()));
        try {
//            processing_text(text);
            setProcessText(text);
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.smoothScrollToPosition(messageList.size() - 1);
        } catch (Exception e) {

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeCsvMessage();
            }
        }).start();
    }

    /**
     * Check permission
     */
    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        if (remainingPermissions.size() > 0) {
            requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
        }
//        }
    }

    private boolean checkPermission() {
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

        }
        return true;
    }

    private void writeCsvMessage() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Vnest_CA");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File csv = new File(folder, "message.csv");
        if (!csv.exists()) {
            try {
                csv.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        String data = "";
        for (Message m : messageList) {
            data += m.getMessage() + ";" + m.getCreatedAt() + ";" + String.valueOf(m.isSender()) + "\n";
        }
        Log.d("writeCsvMessage: ", data);

        FileWriter fw = null;
        try {

            fw = new FileWriter(csv.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read data from file database csv
     */
    private void readCsvMessage() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Vnest_CA").getAbsoluteFile();

        if (folder.exists()) {

            File csv = new File(folder, "message.csv");

            if (csv.exists()) {

                BufferedReader br = null;
                try {
                    String m;
                    br = new BufferedReader(new FileReader(csv));
                    while ((m = br.readLine()) != null) {

                        String[] ms = m.split(";");
                        if (ms.length == 3) {
                            String message = ms[0];
                            long time = Long.parseLong(ms[1]);
                            boolean isUser = Boolean.valueOf(ms[2]);

                            if (!message.equals("Chào bạn, Tôi có thể giúp gì cho bạn!")) {
                                Log.d("readCsvMessage: ", message + " " + String.valueOf(isUser) + " " + String.valueOf(time));
                                messageList.add(new Message(message, isUser, time));
                            }
                        }
                    }
                    mMessageAdapter.notifyDataSetChanged();


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }


        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (checkPermission()) {
                initIfPermissionGranted();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteMessage();
                setProcessText("Tôi có thể giúp gì được cho bạn");
                if (getResources().getBoolean(R.bool.isTablet)) {
                    mRecyclerViewDefaultMainItem.setVisibility(View.VISIBLE);
                    mRecyclerViewAfterProcessItem.setVisibility(View.INVISIBLE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "stop TRIGGER");
        //Start service
        Intent intent = new Intent(this, Trigger.class);
        stopService(intent);

//        if(isRecognitionSpeech){
//            //start Recognition Speech
//            startRecognition();
//        }

    }

    /**
     * Stop the recognizer.
     * Since cancel() does trigger an onResult() call,
     * we cancel the recognizer rather then stopping it.
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "start TRIGGER");
        if (checkPermission()) {
            finishRecognition();
            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
            }
            //Start service
            Intent intent = new Intent(this, Trigger.class);
            startService(intent);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (speechRecognizer != null) {

            speechRecognizer.destroy();

        }

        //Start service
        Intent intent = new Intent(this, Trigger.class);
        stopService(intent);

    }

    private void deleteMessage() {
        messageList.clear();
        messageList.add(new Message("Chào bạn, Tôi có thể giúp gì cho bạn!", false, System.currentTimeMillis()));
        mMessageAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), " Xóa dữ liệu thành công", Toast.LENGTH_LONG).show();

    }

    private void search(String text) {
        Log.e(LOG_TAG, "Key " + text);
        if (text.contains("đường")) {
            String string_start = "đến";
            int start = text.indexOf(string_start) + string_start.length();
            int end = text.length();
            String location = text.substring(start, end);
            Log.e(LOG_TAG, "Key " + location);
            navigation(location);
            sendMessage("Tìm đường đi đến " + location, false);

        } else if (text.contains("gần")) {
            String string_start = "tìm";
            String string_end = "gần";
            int start = text.indexOf(string_start) + string_start.length();
            int end = text.indexOf(string_end);
            String location = text.substring(start, end);
            search_location(location);
            sendMessage("Tìm " + location + "gần nhất", false);
        } else {
            String string_start = "tìm";
            int start = text.indexOf(string_start) + string_start.length();
            int end = text.length();

            String key = text.substring(start, end);

            sendMessage("Search: " + key, false);

            search_google(key);
        }
    }

    /**
     * Search to the google by key search
     *
     * @param key: key search
     */
    private void search_google(String key) {

        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);

        intent.putExtra(SearchManager.QUERY, key);

        startActivity(intent);
    }

    /**
     * Search for the nearest your location
     *
     * @param location: address to find
     */
    private void search_location(String location) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void navigation(String location) {
        location = location.replace(" ", "+");
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void weather() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please waiting....");
        progressDialog.show();

        weather.getCurrentWeatherByGeoCoordinates(latitude, longitude, new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {

                progressDialog.dismiss();

                Date timeSunrise = new Date(currentWeather.getSys().getSunrise() * 1000);
                Date timeSunset = new Date(currentWeather.getSys().getSunset() * 1000);

                DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                String sunrise = dateFormat.format(timeSunrise);
                String sunset = dateFormat.format(timeSunset);


                Log.v("Weather", "Coordinates: " + currentWeather.getCoord().getLat() + ", " + currentWeather.getCoord().getLon() + "\n"
                        + "Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
                        + "Temperature: " + currentWeather.getMain().getTempMax() + "\n"
                        + "Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                        + "City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry() + "\n"
                        + "Time sunrise: " + sunrise + "\n"
                        + "Time sunset: " + sunset + "\n"
                );

                String location = currentWeather.getName() + ", " + currentWeather.getSys().getCountry();
                String description = currentWeather.getWeather().get(0).getDescription();
                String wind = String.valueOf(currentWeather.getWind().getSpeed());
                String tempMax = String.valueOf(currentWeather.getMain().getTempMax());
                String humidity = String.valueOf(currentWeather.getMain().getHumidity());

                show_weather(location, description, tempMax, wind, humidity, sunrise, sunset);
            }

            @Override
            public void onFailure(Throwable throwable) {
                progressDialog.dismiss();
                sendMessage("Lỗi, Không thế tìm kiếm thời tiết tại vị trí của bạn.", false);
                Log.d("Weather", throwable.getMessage(), throwable);
            }
        });

    }


    /**
     * Show dialog weather in location
     *
     * @param location
     * @param description
     * @param tempMax
     * @param wind
     * @param humidity
     * @param sunrise
     * @param sunset
     */
    private void show_weather(String location, String description, String tempMax, String
            wind, String humidity, String sunrise, String sunset) {

        String w = "Thời tiết " + location + " : " + description + " " + tempMax + "\u2103";

        sendMessage(w, false);

        String textSpeech = "Thời tiết " + location + " : " + description + " " + tempMax + "độ xê";
        textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null);


        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.weather);

        dialog.setContentView(R.layout.weather);

        TextView tvLocation, tvDescription, tvTempMax, tvWind, tvHumidity, tvSunrise, tvSunset;
        tvLocation = dialog.findViewById(R.id.tvLocation);
        tvDescription = dialog.findViewById(R.id.tvDescription);
        tvTempMax = dialog.findViewById(R.id.tvTempMax);
        tvWind = dialog.findViewById(R.id.tvWind);
        tvHumidity = dialog.findViewById(R.id.tvHumidity);
        tvSunrise = dialog.findViewById(R.id.tvSunrise);
        tvSunset = dialog.findViewById(R.id.tvSunset);


        tvLocation.setText(location);
        tvDescription.setText(description);
        tvTempMax.setText(tempMax);
        tvWind.setText(wind);
        tvHumidity.setText(humidity);
        tvSunrise.setText(sunrise);
        tvSunset.setText(sunset);

        dialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }

        Log.d("onLocationChanged", String.valueOf(latitude) + " " + String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
