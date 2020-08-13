package ai.kitt.vnest.feature.activitymain;

import ai.api.model.AIContext;
import ai.api.model.AIOutputContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kwabenaberko.openweathermaplib.constants.Lang;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;

import ai.kitt.snowboy.service.TriggerBroadCast;
import ai.kitt.vnest.App;
import ai.kitt.vnest.R;
import ai.kitt.vnest.base.BaseMainActivity;
import ai.kitt.vnest.speechmanager.speechonline.OnResultReady;
import ai.kitt.vnest.speechmanager.speechonline.SpeechRecognizerManager;
import ai.kitt.vnest.basedata.api.model.ActiveCode;
import ai.kitt.vnest.basedata.api.model.ActiveResponse;
import ai.kitt.vnest.basedata.api.repository.ActiveRepo;
import ai.kitt.vnest.feature.activitymain.adapters.DefaultAssistantAdapter;
import ai.kitt.vnest.feature.activitymain.adapters.ItemNavigationAdapter;
import ai.kitt.vnest.basedata.api.model.CarResponse;
import ai.kitt.vnest.basedata.database.sharepreference.VnestSharePreference;
import ai.kitt.vnest.basedata.entity.Audio;
import ai.kitt.vnest.basedata.entity.Message;
import ai.kitt.vnest.basedata.entity.MyAIContext;
import ai.kitt.vnest.basedata.entity.Poi;
import ai.kitt.vnest.basedata.entity.Youtube;
import ai.kitt.vnest.feature.screenhome.AdapterHomeItemDefault;
import ai.kitt.vnest.feature.screenhome.FragmentHome;
import ai.kitt.vnest.feature.screenspeech.FragmentResult;
import ai.kitt.vnest.feature.screensettings.FragmentSettings;
import ai.kitt.vnest.speechmanager.texttospeech.TextToSpeechManager;
import ai.kitt.vnest.util.ConfirmDialog;
import ai.kitt.vnest.util.DialogActiveControl;
import ai.kitt.vnest.util.DialogUtils;
import ai.kitt.vnest.util.AppUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import kun.ktupdatelibrary.DownLoadBroadCast;
import kun.ktupdatelibrary.UpdateChecker;

public class MainActivity extends BaseMainActivity {


    private View bottomSheetLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private FrameLayout fragmentContainer;
    public static boolean isExcecuteText = false;

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private String deviceId;
    private boolean notchangesessionid = false;
    private String currentSessionId;
    private List<AIOutputContext> contexts;
    private RecyclerView mRecyclerViewDefaultAssistant;
    private RecyclerView mRecyclerViewDefaultMainItem;
    private View mCollapseView;
    private Boolean isStartRecognizer;
    private ConstraintLayout mDrawer;
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mainLayout;
    public DownLoadBroadCast downLoadBroadCast;
    private TriggerBroadCast triggerBroadCast;
    private long processTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initIfPermissionGranted() {
        init();
        initAction();
    }

    private void init() {
        initView();
        sendCarInfo();
        viewModel.getLiveDataUpdateResponse().observe(this, this::updateApp);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        initTextToSpeech();


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
                            mDrawerLayout.closeDrawers();
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentSettings.TAG);
                            if (fragment == null) {
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new FragmentSettings(), FragmentSettings.TAG)
                                        .addToBackStack("1")
                                        .commit();
                            }
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

        });
        adapter.setItemClickListener((position, name) -> {
            startResultFragment();
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
    private void initAction() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
        speechRecognizerManager = SpeechRecognizerManager.getInstance(this, new OnResultReady() {
            @Override
            public void onResults(@NotNull ArrayList<String> results) {
                if (isExcecuteText) {
                    return;
                }
                speechRecognizerManager.muteVolume(false);
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
        }, speechRecognizer, () -> viewModel.getLiveDataRebindRecognitionsView().postValue(true));

        if (mCollapseView != null) {
            mCollapseView.setOnClickListener(view -> {
                if (mDrawerLayout.isDrawerOpen(mDrawer)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    private void setUiRecognition(Context context) {
        weather = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));
        weather.setUnits(Units.METRIC);
        weather.setLang(Lang.VIETNAMESE);
    }


    public void finishRecognition() {
        isStartRecognizer = false;
        if (speechRecognizerManager != null) {
            speechRecognizerManager.stopListening();
        }
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
                if (resetContext || calculateResetContext(processTime, currentProcessTime) || contexts == null) {
                    Log.e(LOG_TAG, "============Reset context=============");
                    contexts = null;
                    aiRequest.setResetContexts(true);
                    aiRequest.setContexts(null);
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
                            viewModel.searchPlaces(action, aiRes, this);
                            break;
                        case ACTION_OPEN_VTV:
                            viewModel.openVtv(aiRes, code, this);
                            break;
                        case INPUT_UN_KNOW:
                            searchInputUnknown(aiRes);
                            break;
                        case OPEN_MP3:
                            viewModel.searchMp3(code, aiRes, this);
                            break;
                        case OPEN_YOU_TUBE:
                            viewModel.searchYoutube(aiRes, code, this);
                            break;
                        case ACTION_CALL_FROM_CONTACT:
                            viewModel.callTo(this, aiRes, code, this);
                            break;
                        default:
                            if (text.toLowerCase().contains(KEY_WEATHER)) {
                                weather();
                            } else if (text.toLowerCase().contains(KEY_SEARCH)) {
                                viewModel.search(text, this);
                            } else {
                                sendMessage(NO_DATA_FOUND, false);
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
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "stop TRIGGER");
        if (downLoadBroadCast == null) {
            ProgressDialog waitingDialog = DialogUtils.showProgressDialog(this);
            downLoadBroadCast = DownLoadBroadCast.initBroadCast(this, new DownLoadBroadCast.OnReceive() {
                @Override
                public void onReceiveProgress(int progress) {
                    waitingDialog.dismiss();
                    initProgressDialog();
                    downloadDialog.setMessage("Downloading... ");
                    downloadDialog.setProgress(progress);
                    downloadDialog.show();
                }

                @Override
                public void onFinish() {
                }

                @Override
                public void onWaiting() {
                    waitingDialog.show();
                }

                @Override
                public void onRetry() {
                    updateApp(viewModel.carInfoResponse);
                }
            });
        }

        if (triggerBroadCast == null) {
            triggerBroadCast = TriggerBroadCast.initBroadCast(this, new TriggerBroadCast.OnHandleTrigger() {
                @Override
                public void onActionTurnOn() {
                    if (App.isActivated) {
                        startResultFragment();
                        viewModel.getLiveDataOpenVTV().postValue("-1");
                        viewModel.getLiveDataStartRecord().postValue(true);
                    }
                }

                @Override
                public void onActionTurnOff() {

                }

                @Override
                public void onActionStartApp() {

                }
            }, MainActivity.class);
        }
        if (checkPermission()) {
            if (AppUtil.checkInternetConnection(this) && App.isActivated) {
                startResultFragment();
                viewModel.getLiveDataStartRecord().postValue(true);
            } else {
                startHomeFragment();
            }
        }
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
        resetContext();
        if (checkPermission()) {
            finishRecognition();
            if (speechRecognizer != null) {
                speechRecognizer.stopListening();
            }

        }

    }


    @Override
    protected void onDestroy() {
        Log.e(LOG_TAG, "onDestroy");
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        //Start service
        if (downLoadBroadCast != null) {
            unregisterReceiver(downLoadBroadCast);
        }
        TriggerBroadCast.unregisterBroadCast(this, triggerBroadCast);
        if (downloadDialog != null) {
            downloadDialog.dismiss();
        }
        if (dialogActiveControl != null) {
            dialogActiveControl.dismiss();
        }

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
        textToSpeech.speak(textSpeech, false);
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


}
