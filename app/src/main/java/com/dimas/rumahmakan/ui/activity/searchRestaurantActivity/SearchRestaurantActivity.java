package com.dimas.rumahmakan.ui.activity.searchRestaurantActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.dimas.rumahmakan.BuildConfig;
import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.di.component.ActivityComponent;
import com.dimas.rumahmakan.di.component.DaggerActivityComponent;
import com.dimas.rumahmakan.di.module.ActivityModule;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivity;
import com.dimas.rumahmakan.ui.activity.splashActivity.SplashActivity;
import com.dimas.rumahmakan.ui.adapter.AdapterRestaurant;
import com.dimas.rumahmakan.ui.dialog.DialogDetailRestaurant;
import com.dimas.rumahmakan.ui.dialog.DialogNoInternet;
import com.dimas.rumahmakan.ui.dialog.DialogRequestLocation;
import com.dimas.rumahmakan.ui.util.ErrorLayout;
import com.dimas.rumahmakan.ui.util.LoadingLayout;
import com.dimas.rumahmakan.util.SerializableSave;
import com.dimas.rumahmakan.util.StaticVariabel;
import com.dimas.rumahmakan.util.Unit;
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

import static com.dimas.rumahmakan.util.CheckService.isGpsIson;
import static com.dimas.rumahmakan.util.CheckService.isInternetConnected;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_DETAIL_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_EXPLORE_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SEARCH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SPLASH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.ZOOM_LEVEL;
import static com.dimas.rumahmakan.util.StaticVariabel.createRestaurantMarker;
import static com.dimas.rumahmakan.util.StaticVariabel.createUserMarker;

public class SearchRestaurantActivity extends AppCompatActivity implements SearchRestaurantActivityContract.View {

    @Inject
    public SearchRestaurantActivityContract.Presenter presenter;

    private Context context;
    private Intent intent;
    private int backTo;

    private TextView searchText;
    private TextView chooseList,chooseMap;
    private ImageView imageBack;

    private MapViewLite mapView;

    private RecyclerView restaurantRecycleView;
    private ArrayList<RestaurantModel> restaurantModels = new ArrayList<>();
    private ArrayList<MapMarker> restaurantMarker = new ArrayList<>();
    private AdapterRestaurant adapterRestaurant;
    private int offset = 0,limit = 10;
    private String searchBy = "nama",searchValue = "";
    private String orderBy = "nama",orderDir = "asc";

    private View notFoundLayout;
    private View layoutList;

    private LocationManager locationManager;
    private GeoCoordinates userCoordinate;
    private MapMarker userMarker;

    private LoadingLayout loadingLayout;
    private ErrorLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);
        initWidget(savedInstanceState);
    }

    private void initWidget(Bundle savedInstanceState){
        context = this;
        intent = getIntent();

        if (intent.hasExtra("data")){
            searchValue = intent.getStringExtra("data");
            new SerializableSave(context, StaticVariabel.LAST_SEARCH).save(new SerializableSave.SimpleCache(searchValue));
        } else if (new SerializableSave(context, StaticVariabel.LAST_SEARCH).load() != null){
            searchValue = ((SerializableSave.SimpleCache) new SerializableSave(context, StaticVariabel.LAST_SEARCH).load()).data;
        }

        backTo = intent.getIntExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);

        injectDependency();
        presenter.attach(this);
        presenter.subscribe();

        loadingLayout = new LoadingLayout(context,findViewById(R.id.loading_layout));
        loadingLayout.setMessage(context.getString(R.string.init_here_map));

        errorLayout = new ErrorLayout(context, findViewById(R.id.error_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getIntent());
                finish();
            }
        });
        errorLayout.setMessage(context.getString(R.string.error_common));

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        searchText = findViewById(R.id.search_result_text);
        searchText.setText(context.getString(R.string.search_result_of) + "\"" + searchValue + "\"");

        imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapterRestaurant = new AdapterRestaurant(context, restaurantModels, new Unit<RestaurantModel>() {
            @Override
            public void invoke(RestaurantModel o) {
                Intent i = new Intent(context, DetailRestaurantActivity.class);
                i.putExtra("data",o);
                i.putExtra("back_to",BACK_TO_SEARCH_ACTIVITY);
                startActivity(i);
                finish();
            }
        });
        restaurantRecycleView = findViewById(R.id.restaurant_recycleview);
        restaurantRecycleView.setAdapter(adapterRestaurant);
        restaurantRecycleView.setLayoutManager(new GridLayoutManager(context,2));
        restaurantRecycleView.setVisibility(View.VISIBLE);

        notFoundLayout = findViewById(R.id.not_found_layout);
        notFoundLayout.setVisibility(View.GONE);

        layoutList = findViewById(R.id.layout_list);

        chooseList = findViewById(R.id.choose_list_text);
        chooseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setVisibility(View.GONE);
                layoutList.setVisibility(View.VISIBLE);

                chooseList.setTextColor(ContextCompat.getColor(context,R.color.textColorWhite));
                chooseList.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));

                chooseMap.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));
                chooseMap.setBackgroundColor(ContextCompat.getColor(context,R.color.textColorWhite));

            }
        });
        chooseMap = findViewById(R.id.choose_map_text);
        chooseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setVisibility(View.VISIBLE);
                layoutList.setVisibility(View.GONE);

                chooseList.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));
                chooseList.setBackgroundColor(ContextCompat.getColor(context,R.color.textColorWhite));

                chooseMap.setTextColor(ContextCompat.getColor(context,R.color.textColorWhite));
                chooseMap.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));


            }
        });

        loadMapScene();

        if (!isInternetConnected(context)){
            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) {

                }
            }).show();
        }

        chooseMap.performClick();
    }

    private void getAllRestaurant(GeoCoordinates userCoordinate){

        loadingLayout.setMessage(context.getString(R.string.finding_nearest_restaurant));

        presenter.getAllRestaurant(
                searchBy,searchValue,orderBy,orderDir,offset,limit,true
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
                }
                setLocationManager();
                setTapGestureHandler();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void setLocationManager(){
        if (!isGpsIson(context)){

            errorLayout.show();

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
                                getAllRestaurant(new GeoCoordinates(location.getLatitude(),location.getLongitude()));
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

    private void checkIsEmpty(){
        notFoundLayout.setVisibility(!restaurantModels.isEmpty() ? View.GONE : View.VISIBLE);
        restaurantRecycleView.setVisibility(restaurantModels.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ------------ //

    @Override
    public void onGetAllRestaurant(@Nullable ArrayList<RestaurantModel> all) {
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
        }
        adapterRestaurant.notifyDataSetChanged();
        checkIsEmpty();
    }

    @Override
    public void showProgressAllRestaurant(Boolean show) {
        loadingLayout.setVisibility(show);
    }

    @Override
    public void showErrorAllRestaurant(String error) {
        if (BuildConfig.DEBUG){
            errorLayout.setMessage(error);
        }
        errorLayout.show();
    }

    // ------------ //

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
                    return;
                }

                RestaurantModel r = new RestaurantModel(
                        id,
                        metadata.getString("name"),
                        metadata.getString("address"),
                        metadata.getDouble("distance")
                );

                new DialogDetailRestaurant(context,r, new Unit<Boolean>() {
                            @Override
                            public void invoke(Boolean o) {
                                Intent i = new Intent(context, DetailRestaurantActivity.class);
                                i.putExtra("data",r);
                                i.putExtra("back_to",BACK_TO_SEARCH_ACTIVITY);
                                startActivity(i);
                                finish();
                            }
                        }
                ).show();

            }
        });
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

    @Override
    public void onBackPressed() {
        switch (backTo){
            case BACK_TO_SPLASH_ACTIVITY:
                startActivity(new Intent(context, SplashActivity.class));
                break;
            case BACK_TO_EXPLORE_ACTIVITY:
                startActivity(new Intent(context,ExploreActivity.class));
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
