package ai.kitt.snowboy.database.sharepreference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class VnestSharePreference {
    private static VnestSharePreference sharePreference;
    private final static String SHARE_PREF_NAME = "Vnest";
    private final static String ACTIVE_NAME = "Acitve_code";
    private SharedPreferences sharedPreferences;
    private Context context;

    private VnestSharePreference(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static VnestSharePreference getInstance(Context context) {
        if (sharePreference == null) {
            sharePreference = new VnestSharePreference(context);
        }
        return sharePreference;
    }

    public boolean isHadActiveCode() {
        return getActiveCode() != null;
    }

    public void saveActiveCode(@NonNull String activeCode) {
        sharedPreferences.edit()
                .putString(ACTIVE_NAME, activeCode)
                .apply();
    }

    public String getActiveCode() {
        return sharedPreferences.getString(ACTIVE_NAME, null);
    }
}
