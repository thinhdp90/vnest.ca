package com.vnest.ca.activities;

import ai.api.model.AIContext;
import ai.api.model.AIOutputContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kwabenaberko.openweathermaplib.constants.Lang;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.vnest.ca.OnResultReady;
import com.vnest.ca.R;
import com.vnest.ca.SpeechRecognizerManager;
import com.vnest.ca.adapters.DefaultAssistantAdapter;
import com.vnest.ca.adapters.ItemNavigationAdapter;
import com.vnest.ca.api.model.CarInfo;
import com.vnest.ca.api.reepository.CarRepo;
import com.vnest.ca.entity.Audio;
import com.vnest.ca.entity.Message;
import com.vnest.ca.entity.MyAIContext;
import com.vnest.ca.entity.Poi;
import com.vnest.ca.entity.Youtube;
import com.vnest.ca.feature.home.AdapterHomeItemDefault;
import com.vnest.ca.feature.home.FragmentHome;
import com.vnest.ca.feature.result.FragmentResult;
import com.vnest.ca.triggerword.Trigger;
import com.vnest.ca.util.NavigationUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String LOG_TAG = "VNest";
    private static final int UPDATE_AFTER_PROCESS_TEXT = 4;
    public static final int RESTART_VOICE_RECOGNITION = 1;
    private static final int REQUEST_PERMISSION_CODE = 101;
    private static final String TEXT_TO_SPEECH_RESTART_VOICE_RECORD = "restart_voice";
    private static final String NOT_CHANGE_SESSION_ID = "notchangesessionid";
    private static final String NO_DATA_FOUND = "Hiện không tìm thấy thông tin";
    private static final String KEY_WEATHER = "thời tiết";
    private static final String KEY_SEARCH = "tìm";
    private static final String NO_DATA_FOUND_TRY_AGAIN = "Không tìm thấy kết quả bạn mong muốn! Vui lòng thử lại";
    private static final String NAVIGATE_TO = "Di chuyển tới ";
    private static final String SELECT_YOUR_PLACE = "Vui lòng chọn nơi bạn muốn đến";
    private static final String CAN_NOT_FIND_PLACE = "Không tìm thấy điểm tới";
    private static final String DELETE_DATA_SUCCESS = "Xóa dữ liệu thành công";
    private static final String FIND_WAY_TO = "Tìm đường đi đến ";
    private static final String KEY_OPEN_YOUTUBE = "Mở youtube";
    private static final String KEY_NAVIGATION = "Chỉ đường";
    private static final String KEY_MAINTAIN_SCHEDULE = "Lịch sử sửa chữa";
    private static final String KEY_GASOLINE_HISTORY = "Lịch sử đổ xăng";
    private static final String AI_CONFIG_ACCESS_TOKEN = "73cf2510f55c425eb5f5d8bb20d6d3e7";

    /**
     * Open un_know place
     *
     * @Search_unknow
     **/
    private static final String OPEN_BANK_PLACE_UN_KNOW_SPEECH = "OpenBankPlaceUnknownSpeech";
    private static final String OPEN_DRINK_PLACE_UN_KNOW_SPEECH = "OpenDrinkPlaceUnknownSpeech";
    private static final String OPEN_EAT_PLACE_UN_KNOW_SPEECH = "OpenEatPlaceUnknownSpeech";
    private static final String OPEN_BANK_PLACE_WHATEVER = "OpenBankPlaceWhatever";
    private static final String OPEN_MAP_TO_SPEECH = "OpenMapToSpeech";
    /**
     * search a place
     *
     * @Search_place
     **/
    private static final String OPEN_BANK_PLACE = "OpenBankPlace";
    private static final String OPEN_BANK_PLACE_UN_KNOW = "OpenBankPlaceUnknown";
    private static final String OPEN_DRINK_PLACE = "OpenDrinkPlace";
    private static final String OPEN_DRINK_PLACE_UN_KNOW = "OpenDrinkPlaceUnknown";
    private static final String OPEN_EAT_PLACE = "OpenEatPlace";
    private static final String OPEN_EAT_PLACE_UN_KNOW = "OpenEatPlaceUnknown";
    private static final String OPEN_EAT_PLACE_WHATEVER = "OpenEatPlaceWhatever";
    private static final String OPEN_PLACE = "OpenPlace";
    private static final String OPEN_MAP_TO = "OpenMapTo";

    /**
     * Search: input_un_know
     **/
    private static final String INPUT_UN_KNOW = "input.unknown";
    /**
     * Search: Audio
     */
    private static final String OPEN_MP3 = "Mp3";

    /**
     * Search: Video
     */
    private static final String OPEN_YOU_TUBE = "Youtube";

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

    private View bottomSheetLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout fragmentContainer;

    public TextToSpeech textToSpeech;

    public SpeechRecognizer speechRecognizer;
    public Intent mSpeechRecognizerIntent;

    private AIService aiService;
    public static boolean isExcecuteText = false;

    private LocationManager locationManager;
    private double latitude, longitude;
    private OpenWeatherMapHelper weather;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private String deviceId;
    private boolean notchangesessionid = false;
    private String currentSessionId;
    private List<AIOutputContext> contexts;
    public Handler mHandler;
    public Boolean isShouldProcessText;
    private RecyclerView mRecyclerViewDefaultAssistant;
    private RecyclerView mRecyclerViewDefaultMainItem;
    private View mCollapseView;
    private Boolean isStartRecognizer;
    private ViewModel viewModel;
    private CarRepo carRepo = new CarRepo();
    private ConstraintLayout mDrawer;
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mainLayout;

    private SpeechRecognizerManager speechRecognizerManager;

    public SpeechRecognizerManager getSpeechRecognizerManager() {
        return speechRecognizerManager;
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return speechRecognizer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(ViewModel.class);
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
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 20000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
    }

    private void init() {
        initView();
        deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        carRepo.sendCarInfo(CarInfo.getDefault(deviceId));
        // Get phone's location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        initTextToSpeech();
        final AIConfiguration config = new AIConfiguration(AI_CONFIG_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
    }

    private void initView() {
        fragmentContainer = findViewById(R.id.fragment_container);
        mRecyclerViewDefaultAssistant = findViewById(R.id.recycler_view_def_assistant);
        mCollapseView = findViewById(R.id.view_collapse);
        initLeftNav();

        if (fragmentContainer != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentHome())
                    .addToBackStack(MainActivity.class.getName())
                    .commit();
        }
        initBottomSheet();
    }

    private void initLeftNav() {
        if (mRecyclerViewDefaultAssistant != null) {
            mRecyclerViewDefaultAssistant.setAdapter(new DefaultAssistantAdapter((text, position) -> {
                try {
                    contexts = null;
                    String textSpeech = null;
                    switch (position) {
                        case 0:
                            textSpeech = KEY_OPEN_YOUTUBE;
                            break;
                        case 1:
                            textSpeech = KEY_NAVIGATION;
                            break;
                        case 2:
                            textSpeech = KEY_GASOLINE_HISTORY;
                            break;
                        case 3:
                            textSpeech = KEY_MAINTAIN_SCHEDULE;
                            break;
                        default:
                            break;
                    }
                    if (textSpeech != null) {
                        viewModel.getLiveDataStartRecord().postValue(false);
                        sendMessage(textSpeech, true);
                        processing_text(textSpeech, true);
                        startResultFragment();
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }));
            mRecyclerViewDefaultAssistant.setLayoutManager(new GridLayoutManager(this, 2));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewDefaultAssistant.getContext(),
                    ((GridLayoutManager) Objects.requireNonNull(mRecyclerViewDefaultAssistant.getLayoutManager())).getOrientation());
            mRecyclerViewDefaultAssistant.addItemDecoration(dividerItemDecoration);

            mDrawer = findViewById(R.id.drawer);
            mDrawerLayout = findViewById(R.id.drawerLayout);
            mainLayout = findViewById(R.id.mainLayout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                    float slideX = drawerView.getWidth() * slideOffset;
                    mainLayout.setTranslationX(slideX);
                }
            };
            toggle.syncState();
            mDrawerLayout.addDrawerListener(toggle);
        }
    }

    private void initBottomSheet() {
        bottomSheetLayout = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        mRecyclerViewDefaultMainItem = bottomSheetLayout.findViewById(R.id.mRecyclerView);
        AdapterHomeItemDefault adapter = new AdapterHomeItemDefault(this, getTextToSpeech(), text -> {
            processing_text(text, true);
            startResultFragment();
        });
        adapter.setItemClickListener((position, name) -> {
            viewModel.getLiveDataStartRecord().postValue(false);
            contexts = null;
            sendMessage(name, true);
            processing_text(name, true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        mRecyclerViewDefaultMainItem.setAdapter(adapter);
        mRecyclerViewDefaultMainItem.setLayoutManager(new GridLayoutManager(this, 3));
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheetLayout.getLayoutParams();
        layoutParams.leftMargin = (int) (17 * getResources().getDisplayMetrics().scaledDensity);
        bottomSheetLayout.setLayoutParams(layoutParams);
        bottomSheetLayout.requestLayout();
    }

    public void startResultFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentResult.class.getName());
        if (fragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new FragmentResult(), FragmentResult.class.getName())
                    .addToBackStack(MainActivity.class.getName())
                    .commit();
        }
    }

    private void initAction() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerManager = new SpeechRecognizerManager(this, new OnResultReady() {
            @Override
            public void onResults(@NotNull ArrayList<String> results) {
                if (isExcecuteText) {
                    return;
                }
                finishRecognition();
                speechRecognizerManager.stopListening();
                String text = results.get(0);
                isExcecuteText = true;
                sendMessage(text, true);
                processing_text(text, false);
                Log.d(LOG_TAG, "onResults: " + text);
            }

            @Override
            public void onStreamResult(@NotNull ArrayList<String> partialResults) {

            }
        }, speechRecognizer);
        if (mCollapseView != null) {
            mCollapseView.setOnClickListener(view -> {
                if (mDrawerLayout.isDrawerOpen(mDrawer)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case RESTART_VOICE_RECOGNITION:
                        viewModel.getLiveDataStartRecord().postValue(true);
                        break;
                    case UPDATE_AFTER_PROCESS_TEXT:
                        ArrayList<Poi> poiArrayList = (ArrayList<Poi>) msg.obj;
                        viewModel.getLiveListPoi().postValue(poiArrayList);
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
    }

    public void initTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.d(LOG_TAG, "On Start");
                    finishRecognition();
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.e(LOG_TAG, utteranceId);
                    if (utteranceId.equals(TEXT_TO_SPEECH_RESTART_VOICE_RECORD)) {
                        android.os.Message message = mHandler.obtainMessage(RESTART_VOICE_RECOGNITION);
                        message.sendToTarget();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.d(LOG_TAG, "On Error");
                }
            });
        }, "com.google.android.tts");

        Set<String> a = new HashSet<>();
        a.add("male");
        Voice v = new Voice("vn-VN-x-vnm#male_2-network", Locale.getDefault(), 400, 200, true, a);
        textToSpeech.setVoice(v);
        textToSpeech.setSpeechRate(1.8f);
    }

    public void finishRecognition() {
        isStartRecognizer = false;
    }

    public void processing_text(final String text, Boolean resetContext) {
        Log.d(LOG_TAG, "================= processing_text: " + text);
        Thread thread = new Thread(() -> {
            try {
                AIRequest aiRequest = new AIRequest(text);
                if (!notchangesessionid || currentSessionId == null) {
                    currentSessionId = deviceId + "#" + latitude + "-" + longitude;
                }
                aiRequest.setSessionId(currentSessionId);
                long currentProcessTime = System.currentTimeMillis();
                if (resetContext || calculateResetContext(processTime, currentProcessTime)) {
                    contexts = null;
                    aiRequest.setResetContexts(true);
                }
                processTime = currentProcessTime;
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
                    Log.e(LOG_TAG, "===== action:" + action);
                    contexts = aiRes.getResult().getContexts();
                    String code = null;
                    try {
                        notchangesessionid = aiRes.getResult().getFulfillment().getData().get(NOT_CHANGE_SESSION_ID).getAsBoolean();
                        code = aiRes.getResult().getFulfillment().getData().get("code").toString().replace("\"", "");
                        Log.d(LOG_TAG, "===== " + NOT_CHANGE_SESSION_ID + ": " + notchangesessionid);
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                    Log.e("Actions", action);
                    switch (action) {
                        case OPEN_BANK_PLACE_UN_KNOW_SPEECH:
                        case OPEN_DRINK_PLACE_UN_KNOW_SPEECH:
                        case OPEN_EAT_PLACE_UN_KNOW_SPEECH:
                        case OPEN_MAP_TO_SPEECH:
                        case OPEN_BANK_PLACE_WHATEVER:
                            searchPlaceUnknown(text, aiRes);
                            break;
                        case OPEN_BANK_PLACE:
                        case OPEN_DRINK_PLACE:
                        case OPEN_DRINK_PLACE_UN_KNOW:
                        case OPEN_EAT_PLACE:
                        case OPEN_EAT_PLACE_UN_KNOW:
                        case OPEN_EAT_PLACE_WHATEVER:
                        case OPEN_BANK_PLACE_UN_KNOW:
                        case OPEN_PLACE:
                        case OPEN_MAP_TO:
                            searchPlaces(action, aiRes);
                            break;
                        case INPUT_UN_KNOW:
                            searchInputUnknown(aiRes);
                            break;
                        case OPEN_MP3:
                            searchMp3(code, aiRes);
                            break;
                        case OPEN_YOU_TUBE:
                            searchYoutube(aiRes, code);
                            break;
                        default:
                            if (text.toLowerCase().contains(KEY_WEATHER)) {
                                weather();
                            } else if (text.toLowerCase().contains(KEY_SEARCH)) {
                                search(text);
                            } else {
                                speak(NO_DATA_FOUND, false);
                            }
                    }


                } catch (AIServiceException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isExcecuteText = false;
            }
        });
        thread.start();
    }

    private void searchPlaceUnknown(String text, AIResponse aiRes) throws InterruptedException {
        String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
        sendMessage(textSpeech, false);
        speak(textSpeech, true);
        resetProcessTime();
    }

    private void searchPlaces(String key, AIResponse aiResponse) {
        try {
            JsonArray dataResponse = aiResponse.getResult().getFulfillment().getData().get("pois").getAsJsonArray();
            if (dataResponse.isJsonNull() || dataResponse.size() < 1) {
                sendMessage(NO_DATA_FOUND_TRY_AGAIN, false);
                speak(NO_DATA_FOUND_TRY_AGAIN);
                resetProcessTime();
            } else if (dataResponse.size() >= 1 && getResources().getBoolean(R.bool.isTablet)) {
                final ArrayList<Poi> poiArrayList = new ArrayList<>();
                for (JsonElement element : dataResponse) {
                    poiArrayList.add(gson.fromJson(element, Poi.class));
                }
                ItemNavigationAdapter adapter = new ItemNavigationAdapter(poi -> NavigationUtil.navigationToPoint(poi, MainActivity.this));
                if (key.toLowerCase().equals("openmapto")) {
                    try {
                        sendMessage(NAVIGATE_TO + poiArrayList.get(0).getTitle() + "...", false);
                        Thread.sleep(1000);
                        NavigationUtil.navigationToPoint(poiArrayList.get(0), MainActivity.this);
                        resetProcessTime();
                    } catch (Exception e) {
                        speak(CAN_NOT_FIND_PLACE);
                    }
                } else {
                    sendMessage(SELECT_YOUR_PLACE, false);
                    speak(SELECT_YOUR_PLACE);
                    contexts = null;
                    adapter.setData(poiArrayList);
                    android.os.Message message = mHandler.obtainMessage(UPDATE_AFTER_PROCESS_TEXT);
                    message.obj = poiArrayList;
                    message.sendToTarget();
                    resetProcessTime();
                }


            } else {
                Poi poi = gson.fromJson(dataResponse.get(0), Poi.class);
                sendMessage(NAVIGATE_TO + poi.getTitle() + "...", false);
                NavigationUtil.navigationToPoint(poi, MainActivity.this);
                resetProcessTime();
            }
        } catch (Exception e) {
            Log.e("Error search places", e.getMessage(), e);
        }

    }

    private void searchInputUnknown(AIResponse aiRes) {
        String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
        Log.d(LOG_TAG, "===== textSpeech:" + textSpeech);
        sendMessage(textSpeech, false);
        speak(textSpeech, true);
        resetProcessTime();
    }

    private void searchYoutube(AIResponse aiRes, String code) {
        final String KEY_JSON = "videos";
        if (code.trim().equals("0")) {
            String speech = aiRes.getResult().getFulfillment().getSpeech();
            sendMessage(speech, false);
            speak(speech, true);
        } else {
            Youtube video = gson.fromJson(aiRes.getResult().getFulfillment().getData().get(KEY_JSON).getAsJsonArray().get(0).toString(), Youtube.class);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(video.getHref()));
            intent.setPackage("com.google.android.youtube");
            startActivity(intent);
            sendMessage(video.getHref(), false);
            resetProcessTime();
        }
    }

    private void searchMp3(String code, AIResponse aiRes) {
        final String KEY_JSON = "audios";
        String textSpeech;
        if (code != null) {
            Log.e(LOG_TAG, "======= code:" + code);
            if (code.equals("1")) {
                /**
                 * @HadData
                 * Go to zing mp3
                 * **/
                Audio audio = gson.fromJson(aiRes.getResult().getFulfillment().getData().get(KEY_JSON).getAsJsonArray().get(0).toString(), Audio.class);
                sendMessage(audio.getAlias(), false);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(audio.getLink()));
                intent.setPackage("com.zing.mp3");
                startActivity(intent);
                resetProcessTime();
            } else {
                /**
                 * @ListenAgainSongName
                 * **/
                textSpeech = aiRes.getResult().getFulfillment().getSpeech();
                Log.e(LOG_TAG, "===== textSpeech:" + textSpeech);
                sendMessage(textSpeech, false);
                speak(textSpeech, true);
            }
        } else {
            textSpeech = aiRes.getResult().getFulfillment().getSpeech();
            Log.e(LOG_TAG, "===== textSpeech:" + textSpeech);
            sendMessage(textSpeech, false);
            speak(textSpeech, true);
        }
    }

    private void search(String text) {
        Log.e(LOG_TAG, "Key " + text);
        if (text.contains("đường")) {
            String string_start = "đến";
            int start = text.indexOf(string_start) + string_start.length();
            int end = text.length();
            String location = text.substring(start, end).replace(" ", "+");
            Log.e(LOG_TAG, "Key " + location);
            NavigationUtil.navigationToLocation(location, this);
            sendMessage(FIND_WAY_TO + location, false);
            resetProcessTime();

        } else if (text.contains("gần")) {
            String string_start = KEY_SEARCH;
            String string_end = "gần";
            int start = text.indexOf(string_start) + string_start.length();
            int end = text.indexOf(string_end);
            String location = text.substring(start, end);
            sendMessage("Tìm " + location + "gần nhất", false);
            NavigationUtil.displayLocationToMap(location, this);
            resetProcessTime();
        } else {
            String string_start = KEY_SEARCH;
            int start = text.indexOf(string_start) + string_start.length();
            int end = text.length();
            String key = text.substring(start, end);
            sendMessage("Search: " + key, false);
            searchGoogle(key);
        }
    }

    /**
     * Search to the google by key search
     *
     * @param key: key search
     */
    private void searchGoogle(String key) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, key);
        startActivity(intent);
        resetProcessTime();
    }


    private void speak(String key) {
        textToSpeech.speak(key, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void sendMessage(String text, boolean isUser) {
        viewModel.getLiveDataProcessText().postValue(new Message(text, isUser, Calendar.getInstance().getTimeInMillis()));
    }

    public void speak(String key, boolean shouldRestartRecord) {
        if (!shouldRestartRecord) {
            textToSpeech.speak(key, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, TEXT_TO_SPEECH_RESTART_VOICE_RECORD);
            textToSpeech.speak(key, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    /**
     * Check permission
     */

    private void requestPermission() {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    remainingPermissions.add(permission);
                }
            }
        }
        if (remainingPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), REQUEST_PERMISSION_CODE);
            }
        }
    }

    private boolean checkPermission() {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }

        }
        return true;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (checkPermission()) {
                initIfPermissionGranted();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheetLayout.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            deleteMessage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "stop TRIGGER");
        Intent intent = new Intent(this, Trigger.class);
        stopService(intent);
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
        Toast.makeText(getApplicationContext(), DELETE_DATA_SUCCESS, Toast.LENGTH_LONG).show();
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

                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

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

    private Boolean calculateResetContext(long previousProcessTime, long currentProcessTime) {
        if (previousProcessTime == 0) return true;
        long distance = currentProcessTime - previousProcessTime;
        return TimeUnit.MILLISECONDS.toMinutes(distance) > 1;
    }

    private void resetProcessTime() {
        processTime = 0;
    }

    private long processTime = 0;
}
