package com.vnest.ca.api.reepository;

import android.util.Log;

import com.vnest.ca.api.API;
import com.vnest.ca.api.ApiCall;
import com.vnest.ca.api.model.CarInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarRepo {
    public void sendCarInfo(CarInfo carInfo) {
        ApiCall.getInstance().getApi().carInfo(carInfo).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("Error", "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }
}
