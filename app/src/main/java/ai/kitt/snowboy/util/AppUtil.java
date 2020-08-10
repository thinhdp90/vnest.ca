package ai.kitt.snowboy.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import ai.kitt.snowboy.database.sharepreference.VnestSharePreference;
import ai.kitt.snowboy.entity.Poi;

public class AppUtil {
    public static final String MAPS_NATIVEL_APP_ID = "com.navitel";
    public static final String MAPS_GOOGLE_MAP_APP_ID = "com.google.android.apps.maps";
    public static final String MAPS_VIET_MAP_APP_ID = "com.vietmap";

    public static void navigationOtPointByName(Double latitude, Double longitude, Context context) {
        Uri intentUri = Uri.parse("google.navigation:ll" + latitude + "," + longitude);
        Intent routeIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        String packageName = VnestSharePreference.getInstance(context).getMapAppId();
        routeIntent.setPackage(packageName);
        if (routeIntent.resolveActivity(context.getPackageManager()) == null) {
            // Navitel is not installed, open the page in the market
            intentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
            routeIntent = new Intent(Intent.ACTION_VIEW, intentUri);
            routeIntent.setPackage("com.google.android.apps.maps");
        }
        context.startActivity(routeIntent);
    }

    public static void navigationToPoint(Poi poi, Context context) {
        String location = poi.getGps().getLatitude() + "," + poi.getGps().getLongitude();
        navigationToLocation(location, context);
    }

    public static void navigationToLocation(String location, Context context) {
        Uri gmmIntentUri = Uri.parse("google.navigation:ll=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        String packageName = VnestSharePreference.getInstance(context).getMapAppId();
        mapIntent.setPackage(packageName);
        if (mapIntent.resolveActivity(context.getPackageManager()) == null) {
            gmmIntentUri = Uri.parse("google.navigation:q=" + location);
            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
        }
        context.startActivity(mapIntent);
    }

    public static void displayPointToMap(Poi poi, Context context) {
        displayLocationToMap(poi.getGps().getLatitude() + "," + poi.getGps().getLongitude() + "(" + poi.getTitle() + ")", context);
    }

    public static void displayLocationToMap(String location, Context context) {
        Uri intentUri = Uri.parse("geo:0,0?q=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        String packageName = VnestSharePreference.getInstance(context).getMapAppId();
        mapIntent.setPackage(packageName);
        if (mapIntent.resolveActivity(context.getPackageManager()) == null) {
            mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
        }
        context.startActivity(mapIntent);
    }

    public static void openYoutube(Context context, String url) {
        //        com.vanced.android.youtube
        //        com.google.android.youtube
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.vanced.android.youtube");
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            intent.setPackage("com.google.android.youtube");
        }
        context.startActivity(intent);
    }

    public static String getImei(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String imei = telephonyManager.getDeviceId();
            return imei;
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
        }
        return "";
    }

    public static String getDeviceId(Activity activity) {
        try {
            return Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
        }
        return "";
    }

    public static boolean checkInternetConnection(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;
        return connected;
    }
}


