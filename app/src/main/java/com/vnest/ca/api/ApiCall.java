package com.vnest.ca.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCall {
    private API api;

    private ApiCall() {

    }

    private static ApiCall INSTANCE;

    public static ApiCall getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiCall();
        }
        return INSTANCE;
    }

    public API getApi() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .baseUrl("https://vnest.vn/")
                .build()
                .create(API.class);

    }

    public ApiVtv getApiVtv() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)

                .build();
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .baseUrl("https://vtvgo.vn/")
                .build()
                .create(ApiVtv.class);
    }

    public ApiVtv getFirebaseApiVtv() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)

                .build();
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .baseUrl("https://firebaseinstallations.googleapis.com/")
                .build()
                .create(ApiVtv.class);
    }
}
