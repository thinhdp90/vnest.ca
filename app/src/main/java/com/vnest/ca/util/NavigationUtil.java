package com.vnest.ca.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.vnest.ca.entity.Poi;

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
        String location = poi.getGps().getLatitude() + "," + poi.getGps().getLongitude();
        navigationToLocation(location, context);
    }

    public static void navigationToLocation(String location, Context context) {
        Uri gmmIntentUri = Uri.parse("google.navigation:ll=" + location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.navitel");
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
        mapIntent.setPackage("com.navitel");
        if (mapIntent.resolveActivity(context.getPackageManager()) == null) {
            mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
        }
        context.startActivity(mapIntent);
    }
}
