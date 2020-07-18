package ai.kitt.snowboy.activities;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import ai.kitt.snowboy.api.model.ActiveCode;
import ai.kitt.snowboy.api.model.CarInfo;
import ai.kitt.snowboy.api.model.CarResponse;
import ai.kitt.snowboy.api.repository.ActiveRepo;
import ai.kitt.snowboy.api.repository.CarRepo;
import ai.kitt.snowboy.database.VNestDB;
import ai.kitt.snowboy.entity.Message;
import ai.kitt.snowboy.entity.Poi;

import java.util.List;

//import kun.kt.vtv.VtvFetchLinkStream;
import kun.kt.vtv.VtvFetchLinkStream;

public class ViewModel extends androidx.lifecycle.ViewModel {
    public CarResponse carResponse;
    private Context context;
    private CarRepo carRepo = new CarRepo();
    private ActiveRepo activeRepo = new ActiveRepo();

    private MutableLiveData<Boolean> liveDataStartRecord = new MutableLiveData<>();
    private MutableLiveData<Message> liveDataProcessText = new MutableLiveData<>();
    private MutableLiveData<CarResponse> liveDataUpdateResponse = new MutableLiveData<>();
    private MutableLiveData<List<Poi>> liveListPoi = new MutableLiveData<>();
    private MutableLiveData<List<Message>> listMessLiveData = new MutableLiveData<>();
    private MutableLiveData<String> liveDataOpenVTV = new MutableLiveData<>();


    public ViewModel(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<Poi>> getLiveListPoi() {
        return liveListPoi;
    }

    public MutableLiveData<CarResponse> getLiveDataUpdateResponse() {
        return liveDataUpdateResponse;
    }

    public MutableLiveData<String> getLiveDataOpenVTV() {
        return liveDataOpenVTV;
    }

    public MutableLiveData<Boolean> getLiveDataStartRecord() {
        return liveDataStartRecord;
    }

    public MutableLiveData<List<Message>> getListMessLiveData() {
        return listMessLiveData;
    }

    public MutableLiveData<Message> getLiveDataProcessText() {
        return liveDataProcessText;
    }

    public void saveMessage(Message message) {
        new Thread(() -> {
            try {
                Log.d("Save mess", message.getMessage() + " " + message.isSender());
                VNestDB.getInstances(context)
                        .messageDao()
                        .insert(message);
            } catch (Exception e) {
                Log.e("Error", e.getMessage(), e);
            }
        }).start();
    }

    public void getMessage() {
        new Thread(() -> {
            List<Message> listMessage = VNestDB.getInstances(context)
                    .messageDao()
                    .getAll();
            if (listMessage != null && !listMessage.isEmpty()) {
                listMessLiveData.postValue(listMessage);
            }
        }).start();
    }

    public void sendCarInfo(String deviceId, String imei) {
        carRepo.sendCarInfo(CarInfo.getDefault(deviceId,imei), carResponse -> {
            this.carResponse = carResponse;
            liveDataUpdateResponse.postValue(carResponse);
        });
    }

    public void activeDevice(ActiveCode activeCode, ActiveRepo.ActiveListener activeListener) {
        activeRepo.activeDevice(activeCode, activeListener);
    }


    public void getVtvLink(int channel, VtvFetchLinkStream.OnSuccessListener onSuccessListener) {
        new VtvFetchLinkStream(channel, onSuccessListener).execute();
    }
}
