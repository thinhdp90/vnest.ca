package ai.kitt.vnest.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIOutputContext;
import ai.api.model.AIResponse;
import ai.kitt.vnest.App;
import ai.kitt.vnest.basedata.api.model.ActiveCode;
import ai.kitt.vnest.basedata.api.model.ActiveResponse;
import ai.kitt.vnest.basedata.api.model.CarResponse;
import ai.kitt.vnest.basedata.database.sharepreference.VnestSharePreference;
import ai.kitt.vnest.basedata.entity.Audio;
import ai.kitt.vnest.basedata.entity.Message;
import ai.kitt.vnest.basedata.entity.Poi;
import ai.kitt.vnest.basedata.entity.Youtube;
import ai.kitt.vnest.databinding.ActivityMainBinding;
import ai.kitt.vnest.R;
import ai.kitt.vnest.basedata.api.repository.ActiveRepo;
import ai.kitt.vnest.feature.activitymain.MainActivity;
import ai.kitt.vnest.feature.activitymain.ViewModel;
import ai.kitt.vnest.feature.activitymain.ViewModelFactory;
import ai.kitt.vnest.feature.activitymain.adapters.ItemNavigationAdapter;
import ai.kitt.vnest.feature.screenhome.FragmentHome;
import ai.kitt.vnest.feature.screenspeech.FragmentResult;
import ai.kitt.vnest.speechmanager.speechonline.SpeechRecognizerManager;
import ai.kitt.vnest.speechmanager.texttospeech.TextToSpeechManager;
import ai.kitt.vnest.util.AppUtil;
import ai.kitt.vnest.util.ConfirmDialog;
import ai.kitt.vnest.util.DialogActiveControl;
import ai.kitt.vnest.util.DialogUtils;
import kun.ktupdatelibrary.UpdateChecker;

public abstract class BaseMainActivity extends AppCompatActivity implements LocationListener, TextToSpeechManager.TextToSpeechListener, ViewModel.OnOpenVtvListener,
        ViewModel.OnCallListener, ViewModel.OnSearchMp3Listener, ActiveRepo.ActiveListener, ViewModel.OnSearchYoutubeListener, ViewModel.OnSearchPlacesListener,
        ViewModel.OnOtherSearchListener {
    protected static int callDeviceInfoTimes = 0;
    protected static final String LOG_TAG = "VNest";
    protected static final int REQUEST_PERMISSION_CODE = 101;
    protected static final int PICK_CONTACT_CODE = 102;

    protected static final String TEXT_TO_SPEECH_RESTART_VOICE_RECORD = "restart_voice";
    protected static final String NOT_CHANGE_SESSION_ID = "notchangesessionid";
    protected static final String NO_DATA_FOUND = "Hiện không tìm thấy thông tin";
    protected static final String KEY_WEATHER = "thời tiết";
    protected static final String KEY_SEARCH = "tìm";
    protected static final String NO_DATA_FOUND_TRY_AGAIN = "Không tìm thấy kết quả bạn mong muốn! Vui lòng thử lại";
    protected static final String NAVIGATE_TO = "Di chuyển tới ";
    protected static final String SELECT_YOUR_PLACE = "Vui lòng chọn nơi bạn muốn đến";
    protected static final String CAN_NOT_FIND_PLACE = "Không tìm thấy điểm tới";
    protected static final String DELETE_DATA_SUCCESS = "Xóa dữ liệu thành công";
    protected static final String FIND_WAY_TO = "Tìm đường đi đến ";
    protected static final String KEY_OPEN_YOUTUBE = "Mở youtube";
    protected static final String KEY_NAVIGATION = "Chỉ đường";
    protected static final String KEY_MAINTAIN_SCHEDULE = "Lịch sử sửa chữa";
    protected static final String KEY_GASOLINE_HISTORY = "Lịch sử đổ xăng";
    protected static final String AI_CONFIG_ACCESS_TOKEN = "73cf2510f55c425eb5f5d8bb20d6d3e7";
    /**
     * Open un_know place
     *
     * @Search_unknow
     **/
    protected static final String OPEN_BANK_PLACE_UN_KNOW_SPEECH = "OpenBankPlaceUnknownSpeech";
    protected static final String OPEN_DRINK_PLACE_UN_KNOW_SPEECH = "OpenDrinkPlaceUnknownSpeech";
    protected static final String OPEN_EAT_PLACE_UN_KNOW_SPEECH = "OpenEatPlaceUnknownSpeech";
    protected static final String OPEN_BANK_PLACE_WHATEVER = "OpenBankPlaceWhatever";
    protected static final String OPEN_MAP_TO_SPEECH = "OpenMapToSpeech";
    /**
     * search a place
     *
     * @Search_place
     **/
    protected static final String OPEN_BANK_PLACE = "OpenBankPlace";
    protected static final String OPEN_BANK_PLACE_UN_KNOW = "OpenBankPlaceUnknown";
    protected static final String OPEN_DRINK_PLACE = "OpenDrinkPlace";
    protected static final String OPEN_DRINK_PLACE_UN_KNOW = "OpenDrinkPlaceUnknown";
    protected static final String OPEN_EAT_PLACE = "OpenEatPlace";
    protected static final String OPEN_EAT_PLACE_UN_KNOW = "OpenEatPlaceUnknown";
    protected static final String OPEN_EAT_PLACE_WHATEVER = "OpenEatPlaceWhatever";
    protected static final String OPEN_PLACE = "OpenPlace";
    protected static final String OPEN_MAP_TO = "OpenMapTo";

    /**
     * Search: input_un_know
     **/
    protected static final String INPUT_UN_KNOW = "input.unknown";
    /**
     * Search: Audio
     */
    protected static final String OPEN_MP3 = "Mp3";

    /**
     * Search: Video
     */
    protected static final String OPEN_YOU_TUBE = "Youtube";

    /**
     * Call from contact
     **/
    protected static final String ACTION_CALL_FROM_CONTACT = "CallFromContact";

    /**
     * Open VTV
     **/
    protected static final String ACTION_OPEN_VTV = "PlayVTV";

    protected String[] permissions = {Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SET_ALARM,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE};

    protected ViewModel viewModel;
    protected ActivityMainBinding binding;
    protected AlertDialog progressDialog;
    protected DialogActiveControl dialogActiveControl;
    protected List<AIOutputContext> contexts;
    public TextToSpeechManager textToSpeech;
    protected String deviceId;
    protected double latitude, longitude;
    public SpeechRecognizer speechRecognizer;
    public SpeechRecognizerManager speechRecognizerManager;
    protected ProgressDialog downloadDialog;
    protected OpenWeatherMapHelper weather;
    protected LocationManager locationManager;
    protected AIService aiService;



    public SpeechRecognizerManager getSpeechRecognizerManager() {
        return speechRecognizerManager;
    }

    public TextToSpeechManager getTextToSpeech() {
        return textToSpeech;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(ViewModel.class);
        if (checkPermission()) {
            initIfPermissionGranted();
            initAIMasterService();
        } else {
            requestPermission();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected abstract void initIfPermissionGranted();

    protected void initAIMasterService() {
        final AIConfiguration config = new AIConfiguration(AI_CONFIG_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
    }
    protected void initProgressDialog() {
        if (downloadDialog == null) {
            downloadDialog = new ProgressDialog(this);
            downloadDialog.setMax(100);
            downloadDialog.setMessage("Downloading..");
            downloadDialog.setCancelable(false);
            downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        downloadDialog.show();
    }
    /**
     * Compare current time ti previous time to reset context
     * **/
    protected Boolean calculateResetContext(long previousProcessTime, long currentProcessTime) {
        if (previousProcessTime == 0) return false;
        if (previousProcessTime == -1) return true;
        long distance = currentProcessTime - previousProcessTime;
        return TimeUnit.MILLISECONDS.toMinutes(distance) > 1;
    }

    /**
     * Check permission
     */

    protected void requestPermission() {
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

    protected boolean checkPermission() {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }

        }
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

    protected void initTextToSpeech() {
        textToSpeech = TextToSpeechManager.getInstance(this);
        textToSpeech.setTextToSpeechListener(this);
    }

    protected void resetContext() {
        contexts = null;
    }

    public void sendMessage(String text, boolean isUser) {
        viewModel.getLiveDataProcessText().postValue(new Message(text, isUser, Calendar.getInstance().getTimeInMillis()));
    }

    public void speak(String key, boolean shouldRestartRecord) {
        textToSpeech.speak(key, shouldRestartRecord);
    }

    public void startHomeFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentHome.class.getName());
        if (fragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new FragmentHome(), FragmentHome.class.getName())
                    .addToBackStack(MainActivity.class.getName())
                    .commit();
        }
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
    @SuppressLint("HardwareIds")
    protected void sendCarInfo() {
        deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        // Get phone's location
        if (callDeviceInfoTimes < 1 || !App.isActivated) {
            viewModel.sendCarInfo(deviceId, AppUtil.getImei(this));
        }
    }

    public void updateApp(CarResponse carResponse) {
        if (carResponse == null) {
            new ConfirmDialog.Builder(this, false)
                    .setOnDismissListener(dialog -> {
                        sendCarInfo();
                    })
                    .message(getString(R.string.no_internet_connection))
                    .show();
            return;
        }
        callDeviceInfoTimes++;
        CarResponse.UpdateVersion updateVersion = carResponse.getVersion();
        boolean forceUpdate = updateVersion.getForced() == 1;
        boolean update = updateVersion.getUpdate() == 1;

        if (update) {
            UpdateChecker.checkForDialog(this, updateVersion.getUrl(), forceUpdate, updateVersion.getDescription());
            return;
        }
        App.isActivated = carResponse.isActivatedApp();
        if (App.isForTest) {
            App.isActivated = true;
        }
        if (!App.isActivated) {
            activeApp();
        } else {
            startResultFragment();
            viewModel.getLiveDataStartRecord().postValue(true);
        }
    }


    protected void searchPlaceUnknown(String text, AIResponse aiRes) {
        String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
        sendMessage(textSpeech, false);
        speak(textSpeech, true);
    }

    protected void searchInputUnknown(AIResponse aiRes) {
        String textSpeech = aiRes.getResult().getFulfillment().getSpeech();
        Log.d(LOG_TAG, "===== textSpeech:" + textSpeech);
        sendMessage(textSpeech, false);
        speak(textSpeech, true);
        resetContext();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        Log.d("onLocationChanged", latitude + " " + longitude);
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

    @Override
    public void onRestartVoice() {
        viewModel.getLiveDataStartRecord().postValue(true);

    }

    @Override
    public void onStopVoiceRecord() {
        viewModel.getLiveDataStartRecord().postValue(false);

    }

    @Override
    public void onUpdateMessageAfterProcessText(android.os.Message msg) {
        ArrayList<Poi> poiArrayList = (ArrayList<Poi>) msg.obj;
        viewModel.getLiveListPoi().postValue(poiArrayList);
    }

    @Override
    public void onGetVtvLinkSuccess(int chanel) {
        sendMessage("Đang mở VTV" + chanel + "...", false);
        startResultFragment();
        resetContext();
    }

    @Override
    public void onNoVtvLinkFound(int chanel) {
        sendMessage("Không tìm thấy VTV" + chanel, false);
        speak("Không tìm thấy VTV" + chanel, false);
    }

    @Override
    public void onGetVtvLinkError() {
        speak(NO_DATA_FOUND, false);
        sendMessage(NO_DATA_FOUND, false);
    }

    @Override
    public void onCallSuccess() {
        resetContext();
        Log.e(LOG_TAG, "Call success");
    }

    @Override
    public void onCallError(Exception ex) {
        String message = "Không tìm thấy liên hệ!";
        sendMessage(message, false);
        speak(message, false);
    }

    @Override
    public void onSearchMp3Success(Audio audio) {
        sendMessage(audio.getAlias(), false);
        resetContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(audio.getLink()));
        intent.setPackage("com.zing.mp3");
        if (intent.resolveActivity(getPackageManager()) == null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(audio.getLink()));
        }
        startActivity(intent);
    }

    @Override
    public void onRequestSongNameAgain(String textSpeech) {
        sendMessage(textSpeech, false);
        speak(textSpeech, true);
    }

    @Override
    public void onSearchMp3Error(String textSpeech) {
        sendMessage(textSpeech, false);
        speak(textSpeech, true);
    }

    public void activeApp() {
        startHomeFragment();
        dialogActiveControl = new DialogActiveControl(this, new DialogActiveControl.OnActiveListener() {
            @Override
            public void onAccept(String phone, String activeCode) {
                if (progressDialog == null) {
                    progressDialog = DialogUtils.showProgressDialog(BaseMainActivity.this, false);
                } else {
                    progressDialog.show();
                    dialogActiveControl.dismiss();
                }
                viewModel.activeDevice(new ActiveCode(phone, activeCode,
                                AppUtil.getImei(BaseMainActivity.this),
                                AppUtil.getDeviceId(BaseMainActivity.this)),
                        BaseMainActivity.this);
            }

            @Override
            public void onFail() {

            }
        });
        dialogActiveControl.show();
    }

    @Override
    public void onActiveAppSuccess(ActiveResponse activeCode) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            String code = activeCode.getCode();
            if (code == null) {
                code = "";
            }
            if (code.toLowerCase().endsWith("0")) {
                onActiveAppError();
            } else {
                VnestSharePreference.getInstance(BaseMainActivity.this).saveActiveCode(code + "Vnest");
                App.isActivated = true;
                dialogActiveControl.dismiss();
                startResultFragment();
                viewModel.getLiveDataStartRecord().postValue(true);
            }
        }
    }

    @Override
    public void onActiveAppError() {
        if (progressDialog != null) {
            dialogActiveControl.dismiss();
            progressDialog.dismiss();
            new ConfirmDialog.Builder(BaseMainActivity.this, true)
                    .message(getString(R.string.active_code_fail))
                    .setOnAllowClick(DialogInterface::dismiss)
                    .setOnDismissListener(dialog -> dialogActiveControl.show())
                    .show();
        }
    }

    @Override
    public void onRequestSearchYoutube(String textSpeech) {
        sendMessage(textSpeech, false);
        speak(textSpeech, true);
    }

    @Override
    public void onSearchYoutubeSuccess(Youtube video) {
        AppUtil.openYoutube(this, video.getHref());
        sendMessage(video.getTitle(), false);
        resetContext();
    }

    @Override
    public void onNoYoutubeDataFound() {
        sendMessage(NO_DATA_FOUND, false);
        speak(NO_DATA_FOUND, false);
        resetContext();
    }

    @Override
    public void onSearchPlacesNoDataFoundTryAgain() {
        sendMessage(NO_DATA_FOUND_TRY_AGAIN, false);
        speak(NO_DATA_FOUND_TRY_AGAIN, false);
        resetContext();
    }

    @Override
    public void onSelectPlacesToNavigate(ArrayList<Poi> poiArrayList) {
        sendMessage(SELECT_YOUR_PLACE, false);
        speak(SELECT_YOUR_PLACE, false);
        contexts = null;
        ItemNavigationAdapter adapter = new ItemNavigationAdapter(poi -> AppUtil.navigationToPoint(poi, BaseMainActivity.this));
        adapter.setData(poiArrayList);
        android.os.Message message = TextToSpeechManager.mHandler.obtainMessage(TextToSpeechManager.UPDATE_AFTER_PROCESS_TEXT);
        message.obj = poiArrayList;
        message.sendToTarget();
        resetContext();
    }

    @Override
    public void onOpenMapToPoi(Poi poi) {
        sendMessage(NAVIGATE_TO + poi.getTitle() + "...", false);
        AppUtil.navigationToPoint(poi, BaseMainActivity.this);
        resetContext();
    }

    @Override
    public void onSearchPlacesNoDataFound() {
        sendMessage(CAN_NOT_FIND_PLACE, false);
        speak(CAN_NOT_FIND_PLACE, false);
    }

    @Override
    public void onSearchRoadFromToSuccess(String location) {
        AppUtil.navigationToLocation(location, this);
        sendMessage(FIND_WAY_TO + location, false);
        resetContext();
    }

    @Override
    public void onSearchNearestSuccess(String location) {
        sendMessage("Tìm " + location + "gần nhất", false);
        AppUtil.displayLocationToMap(location, this);
        resetContext();
    }

    @Override
    public void onGoogleSearch(String keySearch) {
        sendMessage("Search: " + keySearch, false);
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, keySearch);
        startActivity(intent);
        resetContext();
    }
}
