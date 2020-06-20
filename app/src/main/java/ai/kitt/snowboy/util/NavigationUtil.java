package ai.kitt.snowboy.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ai.kitt.snowboy.entity.Poi;


public class NavigationUtil {
    public static void navigationOtPointByName(Double latitude, Double longitude, Context context) {
        Uri intentUri = Uri.parse("google.navigation:ll" + latitude + "," + longitude);
        Intent routeIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        routeIntent.setPackage("com.navitel");
        if (routeIntent.resolveActivity(context.getPackageManager()) == null) {
            // Navitel is not installed, open the page in the market
            intentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
            routeIntent = new Intent(Intent.ACTION_VIEW, intentUri);
            routeIntent.setPackage("com.google.android.apps.maps");
        }
        context.startActivity(routeIntent);
    }

    public static void navigationToPoint(Poi poi, Context context) {
        Uri gmmIntentUri = Uri.parse("google.navigation:ll=" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.navitel");
        if (mapIntent.resolveActivity(context.getPackageManager()) == null) {
            // Navitel is not installed, open the page in the market
            gmmIntentUri = Uri.parse("google.navigation:q=" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
        }
        context.startActivity(mapIntent);
    }

    public static void displayPointToMap(Poi poi, Context context) {
//        geo:0,0?q=-33.8666,151.1957(Google+Sydney)
        Uri intentUri = Uri.parse("geo:0,0?q=" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude() + "(" + poi.getTitle() + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        mapIntent.setPackage("com.navitel");
        if (mapIntent.resolveActivity(context.getPackageManager()) == null) {
            // Navitel is not installed, open the page in the market
            mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
        }
        context.startActivity(mapIntent);
    }
}
