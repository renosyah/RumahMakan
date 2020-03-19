package com.dimas.rumahmakan.util;

import android.content.Context;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapViewLite;

public class StaticVariabel {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 122;
    public static final long LOCATION_REFRESH_TIME = 5000;
    public static final float LOCATION_REFRESH_DISTANCE = 0;

    public static final int NO_NEED_TO_BACK = -1;
    public static final int BACK_TO_SPLASH_ACTIVITY = 0;
    public static final int BACK_TO_EXPLORE_ACTIVITY = 1;
    public static final int BACK_TO_SEARCH_ACTIVITY = 2;
    public static final int BACK_TO_DETAIL_ACTIVITY = 3;

    public static final int ZOOM_LEVEL = 16;
    public static final String LAST_SEARCH = "LAST_SEARCH.dat";
    public static final String TUTOR_SWIPE = "TUTOR_SWIPE.dat";

    public static MapMarker createRestaurantMarker(Context c, RestaurantModel restaurantModel){

        MapMarker defaultMarker = new MapMarker(new GeoCoordinates(restaurantModel.Latitude,restaurantModel.Longitude));

        MapImage mapImage = MapImageFactory.fromResource(c.getResources(), R.drawable.marker);
        MapMarkerImageStyle style = new MapMarkerImageStyle();
        style.setScale(1.0f);
        defaultMarker.addImage(mapImage, style);

        Metadata metadata = new Metadata();
        metadata.setInteger("id", restaurantModel.Id);
        metadata.setString("name", restaurantModel.Name);
        metadata.setString("address", restaurantModel.Address);
        metadata.setDouble("distance", restaurantModel.Distance);
        defaultMarker.setMetadata(metadata);

        return defaultMarker;
    }

    public static MapMarker createUserMarker(Context c,GeoCoordinates coordinates){

        MapMarker defaultMarker = new MapMarker(coordinates);

        MapImage mapImage = MapImageFactory.fromResource(c.getResources(),R.drawable.user_current_marker);
        MapMarkerImageStyle style = new MapMarkerImageStyle();
        style.setScale(1.0f);
        defaultMarker.addImage(mapImage, style);

        Metadata metadata = new Metadata();
        metadata.setInteger("id", -1);
        metadata.setString("name", "User");
        metadata.setString("message", "Your Current Location");
        defaultMarker.setMetadata(metadata);

        return defaultMarker;
    }
}
