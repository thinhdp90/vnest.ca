package ai.kitt.snowboy.api.reepository;

import android.util.Log;


import ai.kitt.snowboy.api.ApiCall;
import ai.kitt.snowboy.api.model.CarInfo;

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
