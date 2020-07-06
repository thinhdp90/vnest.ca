package com.vnest.ca.api;

import com.vnest.ca.api.model.VTVResponse;
import com.vnest.ca.api.model.VtvFirebaseRequest;
import com.vnest.ca.api.model.VtvFirebaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiVtv {
    @Headers({
            "authority: vtvgo.vn",
            "origin: https://vtvgo.vn",
            "referer: https://vtvgo.vn/xem-truc-tuyen-kenh-vtv2-2.html",
    })
    @POST("ajax-get-stream")
    Call<VTVResponse> getLink(@Query("type_id") int type_id,
                              @Query("id") int id,
                              @Query("time") long time,
                              @Query("token") String token);

    @Headers({
            "x-goog-api-key: AIzaSyANd1ViQZKY6q4NTJJDnxIA3bG4P6GKVr8",
            "authorization: FIS_v2 2_QpchksDHtiLQ01j7fpOqzdxF3BJjMmckuPocdgjCYKVJMNUiHvJJZpEFbZX_Ug-n"
    })
    @POST("v1/projects/vtvgo-1176/installations/cXXeHCbqa3_Nox9AfeAH3-/authTokens:generate")
    Call<VtvFirebaseResponse> getGenerateToken(@Body VtvFirebaseRequest request);
}
