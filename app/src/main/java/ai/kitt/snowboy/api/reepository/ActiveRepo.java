package ai.kitt.snowboy.api.reepository;

import android.util.Log;

import com.google.gson.stream.MalformedJsonException;

import ai.kitt.snowboy.api.ApiCall;
import ai.kitt.snowboy.api.model.ActiveCode;
import ai.kitt.snowboy.api.model.ActiveResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveRepo {
    public void activeDevice(ActiveCode activeCode, ActiveListener listener) {
        ApiCall.getInstance().getApi().activeCode(activeCode).enqueue(new Callback<ActiveResponse>() {
            @Override
            public void onResponse(Call<ActiveResponse> call, Response<ActiveResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ActiveResponse> call, Throwable t) {
                Log.e("Error",t.getMessage(),t);
                if(t instanceof MalformedJsonException) {
//                    listener.onSuccess("");
                    return;
                }
                listener.onError();
            }
        });
    }

    public interface ActiveListener {
        void onSuccess(ActiveResponse activeCode);

        void onError();
    }
}
