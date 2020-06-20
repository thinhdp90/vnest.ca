package ai.kitt.snowboy.api;

import com.google.gson.GsonBuilder;

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
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .baseUrl("https://vnest.vn/")
                .build()
                .create(API.class);

    }
}
