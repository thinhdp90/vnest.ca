package com.vnest.ca.activities;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.vnest.ca.database.VNestDB;
import com.vnest.ca.entity.Message;
import com.vnest.ca.entity.Poi;

import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private Context context;

    public ViewModel(Context context) {
        this.context = context;
    }

    private MutableLiveData<Boolean> liveDataStartRecord = new MutableLiveData<>();
    private MutableLiveData<Message> liveDataProcessText = new MutableLiveData<>();
    private MutableLiveData<String> liveDataTextToSpeech = new MutableLiveData<>();

    public MutableLiveData<List<Poi>> getLiveListPoi() {
        return liveListPoi;
    }

    public void setLiveListPoi(MutableLiveData<List<Poi>> liveListPoi) {
        this.liveListPoi = liveListPoi;
    }

    private MutableLiveData<List<Poi>> liveListPoi = new MutableLiveData<>();
    private MutableLiveData<List<Message>> listMessLiveData = new MutableLiveData<>();
    private MutableLiveData<Message> insertMessageLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLiveDataStartRecord() {
        return liveDataStartRecord;
    }

    public MutableLiveData<List<Message>> getListMessLiveData() {
        return listMessLiveData;
    }

    public MutableLiveData<Message> getLiveDataProcessText() {
        return liveDataProcessText;
    }

    public MutableLiveData<String> getLiveDataTextToSpeech() {
        return liveDataTextToSpeech;
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
}
