package com.vnest.ca.api;

import com.vnest.ca.api.model.CarInfo;
import com.vnest.ca.api.model.CarResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("search-api/car-info")
    Call<CarResponse> carInfo(@Body CarInfo carInfo);
}
