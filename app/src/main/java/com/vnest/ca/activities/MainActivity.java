package com.vnest.ca.activities;

import ai.api.model.AIContext;
import ai.api.model.AIOutputContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Build;
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
import com.google.gson.JsonObject;
import com.kwabenaberko.openweathermaplib.constants.Lang;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.common.Main;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.vnest.ca.OnResultReady;
import com.vnest.ca.R;
import com.vnest.ca.SpeechRecognizerManager;
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
import com.vnest.ca.util.NavigationUtil;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String LOG_TAG = "VNest";
    private static final int UPDATE_AFTER_PROCESS_TEXT = 4;
    public static final int RESTART_VOICE_RECOGNITION = 1;

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

    public Intent getmSpeechRecognizerIntent() {
        return mSpeechRecognizerIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(ViewModel.class);
        if (checkPermission()) {
            initIfPermissionGranted();
        } else {
            requestPermission();
        }
        Log.e("Width, height", getResources().getDisplayMetrics().widthPixels + " " + getResources().getDisplayMetrics().heightPixels);


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
        // Get phone's location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        final AIConfiguration config = new AIConfiguration("73cf2510f55c425eb5f5d8bb20d6d3e7",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
    }

    private void initView() {
        fragmentContainer = findViewById(R.id.fragment_container);
        mRecyclerViewDefaultAssistant = findViewById(R.id.recycler_view_def_assistant);
        mRecyclerViewDefaultMainItem = findViewById(R.id.recyclerview_def_item);
        mCollapseView = findViewById(R.id.view_collapse);
        if (mRecyclerViewDefaultAssistant != null) {
            mRecyclerViewDefaultAssistant.setAdapter(new DefaultAssistantAdapter(text -> {
                try {
                    textToSpeech.speak("Bạn chưa thể sử dụng " + text, TextToSpeech.QUEUE_FLUSH, null);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
            }));
            mRecyclerViewDefaultAssistant.setLayoutManager(new GridLayoutManager(this, 2));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewDefaultAssistant.getContext(),
                    ((GridLayoutManager) Objects.requireNonNull(mRecyclerViewDefaultAssistant.getLayoutManager())).getOrientation());
            mRecyclerViewDefaultAssistant.addItemDecoration(dividerItemDecoration);

        }

        if (fragmentContainer != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentHome())
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
                processing_text(text);
                Log.d(LOG_TAG, "onResults: " + text);
            }

            @Override
            public void onStreamResult(@NotNull ArrayList<String> partialResults) {

            }
        }, speechRecognizer);
        if (mCollapseView != null) {
            mCollapseView.setOnClickListener(view -> {
                if (mRecyclerViewDefaultAssistant.getVisibility() == View.GONE) {
                    mRecyclerViewDefaultAssistant.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerViewDefaultAssistant.setVisibility(View.GONE);
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

    public void finishRecognition() {
        isStartRecognizer = false;
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
                            Log.e("Error", e.getMessage(), e);
                        }

                        Log.e("Actions", action);
                        switch (action) {
                            case "OpenBankPlaceUnknownSpeech":
                            case "OpenDrinkPlaceUnknownSpeech":
                            case "OpenEatPlaceUnknownSpeech":
                            case "OpenBankPlaceWhatever":
                                search_unknown(text, aiRes);
                                break;
                            case "OpenBankPlace":
                            case "OpenDrinkPlace":
                            case "OpenDrinkPlaceUnknown":
                            case "OpenEatPlace":
                            case "OpenEatPlaceUnknown":
                            case "OpenEatPlaceWhatever":
                            case "OpenBankPlaceUnknown":
                            case "OpenPlace":
                            case "OpenMapTo":
                                search_bank(action, aiRes);
                                break;
                            case "input.unknown":
                                String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
                                Log.d(LOG_TAG, "===== textSpeech:" + textSpeech);
                                textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null);
                                android.os.Message message = mHandler.obtainMessage(RESTART_VOICE_RECOGNITION);
                                message.sendToTarget();
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
        viewModel.getLiveDataTextToSpeech().postValue(textSpeech);
//        textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null);
        sendMessage(textSpeech, false);
        Thread.sleep(3000);
        android.os.Message message = mHandler.obtainMessage(RESTART_VOICE_RECOGNITION);
        message.sendToTarget();
    }

    private void search_bank(String key, AIResponse aiResponse) {
        try {
            JsonArray dataResponse = aiResponse.getResult().getFulfillment().getData().get("pois").getAsJsonArray();
            if (dataResponse.isJsonNull() || dataResponse.size() < 1) {
                Log.e("Data", "null");
                textToSpeech.speak("Không tìm thấy kết quả bạn mong muốn! Vui lòng thử lại", TextToSpeech.QUEUE_FLUSH, null);
                sendMessage("Không tìm thấy kết quả bạn mong muốn! Vui lòng thử lại", false);
            } else if (dataResponse.size() >= 1 && getResources().getBoolean(R.bool.isTablet)) {

                sendMessage("Vui lòng chọn nơi bạn muốn đến", false);
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
                        NavigationUtil.navigationToPoint(poi, MainActivity.this);
                    }
                });
                if (key.toLowerCase().equals("openmapto")) {
                    try {
                        NavigationUtil.navigationToPoint(poiArrayList.get(0), MainActivity.this);
                    } catch (Exception e) {
                        textToSpeech.speak("Không tìm thấy điểm tới", TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    textToSpeech.speak("Vui lòng chọn nơi bạn muốn đến", TextToSpeech.QUEUE_FLUSH, null);
                    adapter.setData(poiArrayList);
                    android.os.Message message = mHandler.obtainMessage(UPDATE_AFTER_PROCESS_TEXT);
                    message.obj = poiArrayList;
                    message.sendToTarget();
                }


            } else {
                Poi poi = gson.fromJson(dataResponse.get(0), Poi.class);
//                Log.e("Gps", "geo:" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
//                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
                NavigationUtil.navigationToPoint(poi, MainActivity.this);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
        }

    }

    public void openMapTo(String action, AIResponse aiResponse) {
        try {
            JsonObject dataResponse = aiResponse.getResult().getFulfillment().getData().get("pois").getAsJsonObject();
//            gson.fromJson(dataResponse,)
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    public void sendMessage(String text, boolean isUser) {
        viewModel.getLiveDataProcessText().postValue(new Message(text, isUser, Calendar.getInstance().getTimeInMillis()));
    }

    /**
     * Check permission
     */

    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
            }
        }
//        }
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
        if (item.getItemId() == R.id.delete) {
            deleteMessage();
            if (getResources().getBoolean(R.bool.isTablet)) {
                mRecyclerViewDefaultMainItem.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "stop TRIGGER");
        //Start service
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
