package com.dimas.rumahmakan.util;

import android.content.Context;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;

// ini adalah class
// yg memiliki variabel dengan nilais statik
// yg akan digunakan berkali-kali
public class StaticVariabel {

    // flag id untuk request lokasi
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 122;

    // waktu refresh untuk update lokasi
    public static final long LOCATION_REFRESH_TIME = 5000;

    // jarak antar lokasi untuk update
    public static final float LOCATION_REFRESH_DISTANCE = 0;

    // status untuk disable activity
    // ketika ingin balik ke activity sebelumnya
    public static final int NO_NEED_TO_BACK = -1;

    // status untuk balik ke activity spash screen
    public static final int BACK_TO_SPLASH_ACTIVITY = 0;

    // status untuk balik ke activity jelajah
    public static final int BACK_TO_EXPLORE_ACTIVITY = 1;

    // status untuk balik ke activity pencarian
    public static final int BACK_TO_SEARCH_ACTIVITY = 2;

    // status untuk balik ke activity detail
    public static final int BACK_TO_DETAIL_ACTIVITY = 3;

    // tinggakatan standar zoom di map
    public static final int ZOOM_LEVEL = 16;

    // nama cache untuk data keyword percarian terakhir
    public static final String LAST_SEARCH = "LAST_SEARCH.dat";

    // nama cache untuk tutorial swiping layout
    public static final String TUTOR_SWIPE = "TUTOR_SWIPE.dat";

    // nama cache untuk tutorial routing
    public static final String TUTOR_ROUTE = "TUTOR_ROUTE.dat";

    // fungsi untuk membuat marker
    // untuk wisata kuliner dengan 3 parameter
    public static MapMarker createRestaurantMarker(Context c, RestaurantModel restaurantModel){

        // membuat instance marker
        MapMarker defaultMarker = new MapMarker(new GeoCoordinates(restaurantModel.Latitude,restaurantModel.Longitude));

        // membuat image yg akan digunakan
        MapImage mapImage = MapImageFactory.fromResource(c.getResources(), R.drawable.marker);

        // deklarasi style
        MapMarkerImageStyle style = new MapMarkerImageStyle();

        // set skala ke 1
        style.setScale(1.0f);

        // tempelkan image dan style
        // ke marker
        defaultMarker.addImage(mapImage, style);

        // membuat instance metadata
        Metadata metadata = new Metadata();

        // set variabel id di metadata
        metadata.setInteger("id", restaurantModel.Id);

        // set name id di metadata
        metadata.setString("name", restaurantModel.Name);

        // set address di metadata
        metadata.setString("address", restaurantModel.Address);

        // set distance di metadata
        metadata.setDouble("distance", restaurantModel.Distance);

        // tambahkan metadata ke marker
        defaultMarker.setMetadata(metadata);

        // balikan marker sebagai nilai balik
        // dari fungsi
        return defaultMarker;
    }

    // fungsi untuk membuat marker
    // untuk posisi user dengan 3 parameter
    public static MapMarker createUserMarker(Context c,GeoCoordinates coordinates){

        // membuat instance marker
        MapMarker defaultMarker = new MapMarker(coordinates);

        // membuat image yg akan digunakan
        MapImage mapImage = MapImageFactory.fromResource(c.getResources(),R.drawable.user_current_marker);

        // deklarasi style
        MapMarkerImageStyle style = new MapMarkerImageStyle();

        // set skala ke 1
        style.setScale(1.0f);

        // tempelkan image dan style
        // ke marker
        defaultMarker.addImage(mapImage, style);

        // membuat instance metadata
        Metadata metadata = new Metadata();

        // set variabel id di metadata
        metadata.setInteger("id", -1);

        // set variabel name di metadata
        metadata.setString("name", "User");

        // set distance di metadata
        metadata.setString("message", "Your Current Location");

        // tambahkan metadata ke marker
        defaultMarker.setMetadata(metadata);

        // balikan marker sebagai nilai balik
        // dari fungsi
        return defaultMarker;
    }
}
