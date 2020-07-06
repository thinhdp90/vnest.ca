package com.vnest.ca.activities;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.vnest.ca.api.ApiCall;
import com.vnest.ca.api.model.CarInfo;
import com.vnest.ca.api.model.CarResponse;
import com.vnest.ca.api.model.VTVResponse;
import com.vnest.ca.api.model.VtvFirebaseRequest;
import com.vnest.ca.api.model.VtvFirebaseResponse;
import com.vnest.ca.api.reepository.CarRepo;
import com.vnest.ca.database.VNestDB;
import com.vnest.ca.entity.Message;
import com.vnest.ca.entity.Poi;

import java.util.List;
import java.util.UUID;

//import kun.kt.vtv.VtvFetchLinkStream;
import kun.kt.vtv.VtvFetchLinkStream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModel extends androidx.lifecycle.ViewModel {
    public CarResponse carResponse;
    private Context context;
    private CarRepo carRepo = new CarRepo();
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

    public void sendCarInfo(String deviceId) {
        carRepo.sendCarInfo(CarInfo.getDefault(deviceId), carResponse -> {
            this.carResponse = carResponse;
            liveDataUpdateResponse.postValue(carResponse);
        });
    }

    public void getVtvUrl() {
        ApiCall.getInstance().getApiVtv().getLink(1, 2, System.currentTimeMillis(), System.currentTimeMillis() + ".c047c1d7e20342a7033474714a962423").enqueue(new Callback<VTVResponse>() {
            @Override
            public void onResponse(Call<VTVResponse> call, Response<VTVResponse> response) {
                Log.e("Response", new Gson().toJson(response.body()));
                Log.e("Response code", response.code() + "");
            }

            @Override
            public void onFailure(Call<VTVResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }

    public void getVtvFirebase() {
        ApiCall.getInstance().getFirebaseApiVtv().getGenerateToken(VtvFirebaseRequest.getDefault()).enqueue(new Callback<VtvFirebaseResponse>() {
            @Override
            public void onResponse(Call<VtvFirebaseResponse> call, Response<VtvFirebaseResponse> response) {
                Log.e("REsponse", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<VtvFirebaseResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }

    public void getVtvLink(int channel, VtvFetchLinkStream.OnSuccessListener onSuccessListener) {
        new VtvFetchLinkStream(channel, onSuccessListener).execute();
    }
}
