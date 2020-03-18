package com.dimas.rumahmakan.ui.activity.routingActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.di.component.ActivityComponent;
import com.dimas.rumahmakan.di.component.DaggerActivityComponent;
import com.dimas.rumahmakan.di.module.ActivityModule;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivity;
import com.dimas.rumahmakan.ui.activity.searchRestaurantActivity.SearchRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.splashActivity.SplashActivity;
import com.dimas.rumahmakan.ui.dialog.DialogNoInternet;
import com.dimas.rumahmakan.ui.dialog.DialogRequestLocation;
import com.dimas.rumahmakan.util.Unit;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.GestureType;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.CameraObserver;
import com.here.sdk.mapviewlite.CameraUpdate;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapPolyline;
import com.here.sdk.mapviewlite.MapPolylineStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.Padding;
import com.here.sdk.mapviewlite.PixelFormat;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.dimas.rumahmakan.util.CheckService.isGpsIson;
import static com.dimas.rumahmakan.util.CheckService.isInternetConnected;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_DETAIL_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_EXPLORE_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SEARCH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SPLASH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.LOCATION_REFRESH_DISTANCE;
import static com.dimas.rumahmakan.util.StaticVariabel.LOCATION_REFRESH_TIME;
import static com.dimas.rumahmakan.util.StaticVariabel.ZOOM_LEVEL;
import static com.dimas.rumahmakan.util.StaticVariabel.createRestaurantMarker;
import static com.dimas.rumahmakan.util.StaticVariabel.createUserMarker;

public class RoutingActivity extends AppCompatActivity  implements RoutingActivityContract.View {

    @Inject
    public RoutingActivityContract.Presenter presenter;

    private Context context;
    private Intent intent;
    private int backTo;

    private RestaurantModel destinationRestaurant;

    private MapViewLite mapView;

    private ImageView back;
    private TextView goingTo;
    private TextView durration;

    private ImageView trackingUserRoute;
    private ImageView moveToUserLocation;
    private Boolean isTracking = false, isUpdateRoute = true;

    private View loadingMapLayout;
    private View loadingRoutingLayout;

    private LocationManager locationManager;
    private GeoCoordinates userCoordinate;
    private MapMarker userMarker;

    private RoutingEngine routingEngine;
    private MapPolyline routeMapPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);
        initWidget(savedInstanceState);
    }

    private void initWidget(Bundle savedInstanceState) {
        context = this;
        intent = getIntent();

        destinationRestaurant = (RestaurantModel) intent.getSerializableExtra("data");
        backTo = intent.getIntExtra("back_to", BACK_TO_EXPLORE_ACTIVITY);

        injectDependency();
        presenter.attach(this);
        presenter.subscribe();

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        back = findViewById(R.id.back_image);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        goingTo = findViewById(R.id.to_destination_text);
        goingTo.setText(context.getString(R.string.going_to) + "" + destinationRestaurant.Name);

        durration = findViewById(R.id.route_duration_text);

        trackingUserRoute = findViewById(R.id.tracking_user_route_image);
        trackingUserRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTracking = !isTracking;

                trackingUserRoute.setBackground(
                        isTracking ? ContextCompat.getDrawable(context, R.drawable.rounded_shape) :
                                ContextCompat.getDrawable(context, R.drawable.rounded_shape_white)
                );
            }
        });

        moveToUserLocation = findViewById(R.id.current_user_location_image);
        moveToUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userCoordinate != null) {
                    mapView.getCamera().setTarget(userCoordinate);
                    mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                }
            }
        });

        loadingMapLayout = findViewById(R.id.loading_map_layout);
        loadingMapLayout.setVisibility(View.VISIBLE);

        loadingRoutingLayout = findViewById(R.id.loading_route_layout);
        loadingRoutingLayout.setVisibility(View.VISIBLE);

        loadMapScene();

        if (!isInternetConnected(context)) {
            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) {

                }
            }).show();
        }
    }


    private void loadMapScene() {

        try {
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException ignore) {

        }

        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {

                    // akakom coordinate
                    mapView.getCamera().setTarget(new GeoCoordinates(-7.792810, 110.408499));
                    mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                }

                mapView.getMapScene().addMapMarker(createRestaurantMarker(context,destinationRestaurant));

                setTapGestureHandler();
                setLocationManager();
            }
        });
    }
    private void setTapGestureHandler() {

        // disable gesture
        mapView.getGestures().disableDefaultAction(GestureType.DOUBLE_TAP);
        mapView.getGestures().disableDefaultAction(GestureType.TWO_FINGER_TAP);
        mapView.getGestures().disableDefaultAction(GestureType.TWO_FINGER_PAN);

        // disable rotation
        mapView.getCamera().addObserver(new CameraObserver() {
            @Override
            public void onCameraUpdated(@NonNull CameraUpdate cameraUpdate) {
                if (cameraUpdate.bearing != 0) {
                    mapView.getCamera().setBearing(0);
                }
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void setLocationManager(){
        if (!isGpsIson(context)){
            new DialogRequestLocation(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).show();
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_REFRESH_TIME,LOCATION_REFRESH_DISTANCE, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    if (!isUpdateRoute){
                        return;
                    }

                    loadingMapLayout.setVisibility(View.GONE);
                    userCoordinate = new GeoCoordinates(location.getLatitude(),location.getLongitude());

                    showRouting(new Waypoint(new GeoCoordinates(userCoordinate.latitude,userCoordinate.longitude)),
                            new Waypoint(new GeoCoordinates(destinationRestaurant.Latitude,destinationRestaurant.Longitude))
                    );

                    if (isTracking){
                        mapView.getCamera().setTarget(userCoordinate);
                    }

                    try {

                        if (userMarker != null){
                            mapView.getMapScene().removeMapMarker(userMarker);
                            userMarker = null;
                        }

                        userMarker = createUserMarker(context,userCoordinate);
                        mapView.getMapScene().addMapMarker(userMarker);

                    }catch (NullPointerException ignore){}
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            },null);


    }

    private void showRouting(Waypoint startWaypoint, Waypoint destinationWaypoint){

        if (routingEngine == null) {
            return;
        }

        List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));
        routingEngine.calculateRoute(
                waypoints,
                new RoutingEngine.CarOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {

                        loadingRoutingLayout.setVisibility(View.GONE);
                        removeCurrentRoute();

                        if (routingError == null && routes != null && routes.get(0) != null) {

                            long minute = routes.get(0).getDurationInSeconds() / 60;
                            long hour = minute / 60;

                            int meter = routes.get(0).getLengthInMeters();
                            int km = meter / 1000;

                            durration.setText(hour + " "+ context.getString(R.string.hour) + " " + minute + " "+ context.getString(R.string.minute) + " (" + km + " Km)");

                            try {

                                GeoPolyline routeGeoPolyline = new GeoPolyline(routes.get(0).getPolyline());
                                MapPolylineStyle mapPolylineStyle = new MapPolylineStyle();
                                mapPolylineStyle.setColor(ContextCompat.getColor(context,R.color.colorPrimaryLight), PixelFormat.ARGB_8888);
                                mapPolylineStyle.setWidth(10);
                                routeMapPolyline = new MapPolyline(routeGeoPolyline, mapPolylineStyle);
                                mapView.getMapScene().addMapPolyline(routeMapPolyline);

                            } catch (InstantiationErrorException ignore) { }

                        }
                    }
                });
    }

    private void removeCurrentRoute(){
        try {
            if (mapView != null && routeMapPolyline != null){
                mapView.getMapScene().removeMapPolyline(routeMapPolyline);
                routeMapPolyline = null;
            }
        }catch (NullPointerException ignore){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isUpdateRoute = false;
        mapView.onDestroy();
        presenter.unsubscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onBackPressed() {
        isUpdateRoute = false;
        switch (backTo){
            case BACK_TO_SPLASH_ACTIVITY:
                startActivity(new Intent(context, SplashActivity.class));
                break;
            case BACK_TO_EXPLORE_ACTIVITY:
                startActivity(new Intent(context, ExploreActivity.class));
                break;
            case BACK_TO_SEARCH_ACTIVITY:
                startActivity(new Intent(context, SearchRestaurantActivity.class));
                break;
            case BACK_TO_DETAIL_ACTIVITY:
                startActivity(new Intent(context, DetailRestaurantActivity.class));
                break;
        }
        finish();
    }

    private void injectDependency(){
        ActivityComponent listcomponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();

        listcomponent.inject(this);
    }
}
