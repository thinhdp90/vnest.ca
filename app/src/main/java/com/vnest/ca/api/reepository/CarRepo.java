package com.vnest.ca.api.reepository;

import android.util.Log;

import com.google.gson.Gson;
import com.vnest.ca.api.API;
import com.vnest.ca.api.ApiCall;
import com.vnest.ca.api.model.CarInfo;
import com.vnest.ca.api.model.CarResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarRepo {
    public void sendCarInfo(CarInfo carInfo,OnResponseListener onResponseListener) {
        ApiCall.getInstance().getApi().carInfo(carInfo).enqueue(new Callback<CarResponse>() {
            @Override
            public void onResponse(Call<CarResponse> call, Response<CarResponse> response) {
                Log.e("onResponse", new Gson().toJson(response.body()));
                onResponseListener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<CarResponse> call, Throwable t) {
                Log.e("Error", t.getMessage(), t);
            }
        });
    }

    public interface OnResponseListener {
        void onResponse(CarResponse carResponse);
    }
}
