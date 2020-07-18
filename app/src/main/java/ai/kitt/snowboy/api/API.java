package ai.kitt.snowboy.api;

import ai.kitt.snowboy.api.model.ActiveCode;
import ai.kitt.snowboy.api.model.ActiveResponse;
import ai.kitt.snowboy.api.model.CarInfo;
import ai.kitt.snowboy.api.model.CarResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("search-api/car-info")
    Call<CarResponse> carInfo(@Body CarInfo carInfo);

    //https://vnest.vn/
    @POST("search-api/activate")
    Call<ActiveResponse> activeCode(@Body ActiveCode activeCode);
}
