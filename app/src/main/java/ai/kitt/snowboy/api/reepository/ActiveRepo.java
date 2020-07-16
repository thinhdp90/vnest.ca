package ai.kitt.snowboy.api.reepository;

import android.util.Log;

import com.google.gson.stream.MalformedJsonException;

import ai.kitt.snowboy.api.ApiCall;
import ai.kitt.snowboy.api.model.ActiveCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveRepo {
    public void activeDevice(ActiveCode activeCode, ActiveListener listener) {
        ApiCall.getInstance().getApi().activeCode(activeCode).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Error",t.getMessage(),t);
                if(t instanceof MalformedJsonException) {
                    listener.onSuccess("");
                    return;
                }
                listener.onError();
            }
        });
    }

    public interface ActiveListener {
        void onSuccess(String activeCode);

        void onError();
    }
}
