package com.dimas.rumahmakan.ui.activity.exploreActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dimas.rumahmakan.BuildConfig;
import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.di.component.ActivityComponent;
import com.dimas.rumahmakan.di.component.DaggerActivityComponent;
import com.dimas.rumahmakan.di.module.ActivityModule;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.searchRestaurantActivity.SearchRestaurantActivity;
import com.dimas.rumahmakan.ui.adapter.AdapterSwipeStackRestaurant;
import com.dimas.rumahmakan.ui.dialog.DialogAboutApp;
import com.dimas.rumahmakan.ui.dialog.DialogNoInternet;
import com.dimas.rumahmakan.ui.dialog.DialogRequestLocation;
import com.dimas.rumahmakan.ui.util.ErrorLayout;
import com.dimas.rumahmakan.ui.util.LoadingLayout;
import com.dimas.rumahmakan.ui.util.TutorialLayout;
import com.dimas.rumahmakan.util.StaticVariabel;
import com.dimas.rumahmakan.util.Unit;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.gestures.GestureType;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.CameraObserver;
import com.here.sdk.mapviewlite.CameraUpdate;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PickMapItemsCallback;
import com.here.sdk.mapviewlite.PickMapItemsResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import javax.inject.Inject;

import link.fls.swipestack.SwipeStack;

import static com.dimas.rumahmakan.util.CheckService.isGpsIson;
import static com.dimas.rumahmakan.util.CheckService.isInternetConnected;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_EXPLORE_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.dimas.rumahmakan.util.StaticVariabel.ZOOM_LEVEL;
import static com.dimas.rumahmakan.util.StaticVariabel.createRestaurantMarker;
import static com.dimas.rumahmakan.util.StaticVariabel.createUserMarker;

public class ExploreActivity extends AppCompatActivity implements ExploreActivityContract.View{

    @Inject
    public ExploreActivityContract.Presenter presenter;

    private Context context;
    private MapViewLite mapView;

    private EditText searchRestaurant;

    private SwipeStack restaurantStackView;
    private ArrayList<RestaurantModel> restaurantModels = new ArrayList<>();
    private ArrayList<MapMarker> restaurantMarker = new ArrayList<>();
    private AdapterSwipeStackRestaurant adapterSwipeStackRestaurant;
    private int offset = 0,limit = 10;
    private String searchBy = "nama",searchValue = "";
    private double range = BuildConfig.MIN_RADIUS;

    private Button reloadButton;

    private LocationManager locationManager;
    private GeoCoordinates userCoordinate;
    private MapMarker userMarker;

    private TutorialLayout tutorialLayout;
    private LoadingLayout loadingMessageLayout;
    private ErrorLayout errorMessageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        initWidget(savedInstanceState);
    }

    private void initWidget(Bundle savedInstanceState){
        context = this;

        injectDependency();
        presenter.attach(this);
        presenter.subscribe();

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        tutorialLayout = new TutorialLayout(context, StaticVariabel.TUTOR_SWIPE, findViewById(R.id.tutorial_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialLayout.hide();
            }
        });
        tutorialLayout.setMessage(context.getString(R.string.tutorial_swipe));
        tutorialLayout.setImage(R.drawable.tutorial_swipe);

        loadingMessageLayout = new LoadingLayout(context,findViewById(R.id.loading_layout));
        loadingMessageLayout.setMessage(context.getString(R.string.init_here_map));

        errorMessageLayout = new ErrorLayout(context, findViewById(R.id.error_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getIntent());
                finish();
            }
        });
        errorMessageLayout.setMessage(context.getString(R.string.error_common));

        searchRestaurant = findViewById(R.id.search_restaurant_edittext);
        searchRestaurant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent i = new Intent(context, SearchRestaurantActivity.class);
                    i.putExtra("data",searchRestaurant.getText().toString());
                    i.putExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);
                    startActivity(i);
                    finish();
                    return true;
                }
                return false;
            }
        });

        reloadButton = findViewById(R.id.button_reload);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRestaurantMarker();

                restaurantMarker.clear();
                restaurantModels.clear();
                adapterSwipeStackRestaurant.notifyDataSetChanged();

                getAllNearestRestaurant(userCoordinate,false);

                reloadButton.setVisibility(View.GONE);
                restaurantStackView.setVisibility(View.VISIBLE);
            }
        });

        adapterSwipeStackRestaurant = new AdapterSwipeStackRestaurant(context, restaurantModels, new Unit<RestaurantModel>() {
            @Override
            public void invoke(RestaurantModel o) {
                Intent i = new Intent(context, DetailRestaurantActivity.class);
                i.putExtra("data",o);
                i.putExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);
                startActivity(i);
                finish();
            }
        });
        restaurantStackView = findViewById(R.id.restaurant_swipe_stack);
        restaurantStackView.setAdapter(adapterSwipeStackRestaurant);
        restaurantStackView.setListener(new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(int position) {
                this.onViewSwipedToRight(position);
            }

            @Override
            public void onViewSwipedToRight(int position) {
                if (restaurantModels.size() <= position + 1){
                    return;
                }

                RestaurantModel r = restaurantModels.get(position + 1);
                mapView.getCamera().setTarget(new GeoCoordinates(r.Latitude,r.Longitude));
                mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                mapView.getCamera().setTargetAnchorPoint(new Anchor2D(0.5F, 0.3F));
            }

            @Override
            public void onStackEmpty() {
                reloadButton.setVisibility(View.VISIBLE);
                restaurantStackView.setVisibility(View.GONE);
            }
        });

        reloadButton.setVisibility(View.GONE);
        restaurantStackView.setVisibility(View.VISIBLE);

        requestLocationPermission(new Unit<Boolean>() {
            @Override
            public void invoke(Boolean o) {
                loadMapScene();
            }
        });

        if (!isInternetConnected(context)){

            errorMessageLayout.show();

            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) {

                }
            }).show();
        }
    }

    private void getAllNearestRestaurant(GeoCoordinates userCoordinate, boolean loading){

        loadingMessageLayout.setMessage(context.getString(R.string.finding_nearest_restaurant));

        presenter.getAllNearestRestaurant(
                userCoordinate.latitude,userCoordinate.longitude,range,searchBy,searchValue,offset,limit,loading
        );
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {

                    // akakom coordinate
                    mapView.getCamera().setTarget(new GeoCoordinates(-7.792810, 110.408499));
                    mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                    mapView.getCamera().setTargetAnchorPoint(new Anchor2D(0.5F, 0.3F));
                }
                setLocationManager();
                setTapGestureHandler();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void setLocationManager(){
        if (!isGpsIson(context)){

            errorMessageLayout.show();

            new DialogRequestLocation(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) {

                }
            }).show();
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null)
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            // only call once
                            if (userCoordinate == null){
                                getAllNearestRestaurant(new GeoCoordinates(location.getLatitude(),location.getLongitude()),true);
                            }

                            userCoordinate = new GeoCoordinates(location.getLatitude(),location.getLongitude());

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


    // ------------ //

    @Override
    public void onGetAllNearestRestaurant(@Nullable ArrayList<RestaurantModel> all) {
        if (all != null){
            for (RestaurantModel r : all){
                r.Distance = r.calculateDistance(userCoordinate);
                MapMarker m = createRestaurantMarker(context,r);
                restaurantMarker.add(m);
                mapView.getMapScene().addMapMarker(m);
            }
            restaurantModels.addAll(all);
        }
        if (restaurantModels.size() > 0) {
            mapView.getCamera().setTarget(new GeoCoordinates(restaurantModels.get(0).Latitude,restaurantModels.get(0).Longitude));
            mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
            mapView.getCamera().setTargetAnchorPoint(new Anchor2D(0.5F, 0.3F));
        }
        adapterSwipeStackRestaurant.notifyDataSetChanged();
    }

    @Override
    public void showProgressAllNearestRestaurant(Boolean show) {
        loadingMessageLayout.setVisibility(show);
    }

    @Override
    public void showErrorAllNearestRestaurant(String error) {
        if (BuildConfig.DEBUG){
            errorMessageLayout.setMessage(error);
        }
        errorMessageLayout.show();
    }

    // ------------ //

    private void removeRestaurantMarker(){
        for (MapMarker m : restaurantMarker){
            mapView.getMapScene().removeMapMarker(m);
        }
    }

    private void setTapGestureHandler() {

        // on map tap
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(@NotNull Point2D touchPoint) {
                pickMapMarker(touchPoint);
            }
        });

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

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 2;
        mapView.pickMapItems(touchPoint, radiusInPixel, new PickMapItemsCallback() {
            @Override
            public void onMapItemsPicked(@Nullable PickMapItemsResult pickMapItemsResult) {
                if (pickMapItemsResult == null) {
                    return;
                }

                MapMarker mapMarker = pickMapItemsResult.getTopmostMarker();
                if (mapMarker == null) {
                    return;
                }

                Metadata metadata = mapMarker.getMetadata();
                if (metadata == null) {
                    return;
                }

                int id = metadata.getInteger("id");
                if (id == -1){
                    new DialogAboutApp(context, new Unit<Boolean>() {
                        @Override
                        public void invoke(Boolean o) {

                        }
                    }).show();
                    return;
                }

                Intent i = new Intent(context, DetailRestaurantActivity.class);
                i.putExtra("data",new RestaurantModel(id));
                i.putExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startActivity(new Intent(context,ExploreActivity.class));
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void requestLocationPermission(Unit<Boolean> doIt){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            doIt.invoke(true);
        }
    }
    private void injectDependency(){
        ActivityComponent listcomponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();

        listcomponent.inject(this);
    }
}
