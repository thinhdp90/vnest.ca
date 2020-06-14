package com.vnest.ca.api;

import com.vnest.ca.api.model.CarInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("search-api/car-info")
    Call<String> carInfo(@Body CarInfo carInfo);
}
