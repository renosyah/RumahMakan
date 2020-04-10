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
import com.dimas.rumahmakan.ui.util.ErrorLayout;
import com.dimas.rumahmakan.ui.util.LoadingLayout;
import com.dimas.rumahmakan.ui.util.TutorialLayout;
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
import static com.dimas.rumahmakan.util.StaticVariabel.TUTOR_ROUTE;
import static com.dimas.rumahmakan.util.StaticVariabel.ZOOM_LEVEL;
import static com.dimas.rumahmakan.util.StaticVariabel.createRestaurantMarker;
import static com.dimas.rumahmakan.util.StaticVariabel.createUserMarker;

// ini adalah activity
// yg digunakan untuk
// menampilkan rute
// dari lokasi user ke
// lokasi wisatakuliner
public class RoutingActivity extends AppCompatActivity  implements RoutingActivityContract.View {

    // presenter yg akan diinject
    @Inject
    public RoutingActivityContract.Presenter presenter;

    // deklarasi konteks
    private Context context;

    // deklarasi intent
    private Intent intent;

    // deklarasi kode untuk kembali
    private int backTo;

    // deklarasi info data wisata kuliner
    private RestaurantModel destinationRestaurant;

    // deklarasi mapview
    private MapViewLite mapView;

    // deklarasi tombol kembali
    private ImageView back;

    // deklarasi teks tujuan
    private TextView goingTo;

    // deklarasi teks jangka waktu tempuh
    private TextView durration;

    // deklarasi tombol tracking user
    private ImageView trackingUserRoute;

    // deklarasi tombol untuk mengarah ke lokasi user
    private ImageView moveToUserLocation;

    // deklarasi flag status
    // apakah tracking user aktif
    // dan update  rute aktif
    private Boolean isTracking = false, isUpdateRoute = true;

    //deklarasi lokasi manajer
    private LocationManager locationManager;

    // deklarasi data kordinat posisi user
    private GeoCoordinates userCoordinate;

    // deklarasi data marker user
    private MapMarker userMarker;

    // deklarasi pembuat rute
    // yg nantinya akan mengkalkulasi rute
    private RoutingEngine routingEngine;

    // deklarasi data route yg ditampilkan
    private MapPolyline routeMapPolyline;

    // deklarasi layout tutorial
    private TutorialLayout tutorialLayout;

    // deklarasi layout loading
    private LoadingLayout loadingLayout;

    // deklarasi layout error
    private ErrorLayout errorLayout;


    // ini adalah fungsi yg akan dipanggil
    // pertama kali saat activity dijalankan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set view yg digunakan
        setContentView(R.layout.activity_routing);

        // memanggil fungsi inisiasi
        initWidget(savedInstanceState);
    }

    // fungsi inisialisasi
    private void initWidget(Bundle savedInstanceState) {

        // inisialisasi konteks
        context = this;

        // inisialisasi intent
        intent = getIntent();

        // inisialisasi data info
        // wisata kuliner
        // yg didapat dari intent
        destinationRestaurant = (RestaurantModel) intent.getSerializableExtra("data");

        // inisialisasi code
        // untuk kembali ke activity
        // sebelumnya
        // yg didapat dari intent
        backTo = intent.getIntExtra("back_to", BACK_TO_EXPLORE_ACTIVITY);

        // memanngil fungsi inject dependensi
        injectDependency();

        // memanggil fungsi inject activity
        presenter.attach(this);

        // memanngil fungsi subscribe
        presenter.subscribe();

        // inisialisasi mapview
        mapView = findViewById(R.id.map_view);

        // panggil fungsi on create mapview
        mapView.onCreate(savedInstanceState);

        // inisialisasi tombol kembali
        back = findViewById(R.id.back_image);

        // saat ditekan maka
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // akan memanggil fungsi
                // default tombol kembali
                onBackPressed();
            }
        });

        // inisialisasi text tujuan
        goingTo = findViewById(R.id.to_destination_text);

        // menampilkan teks nama tujuan
        goingTo.setText(context.getString(R.string.going_to) + "" + destinationRestaurant.Name);

        // inisialisasi durasi tempuh
        durration = findViewById(R.id.route_duration_text);

        // inisialisasi tombol tracking
        trackingUserRoute = findViewById(R.id.tracking_user_route_image);

        // pada saat ditekan maka
        trackingUserRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tracking akan aktif jika tidak
                // dan sebaliknya jika iya
                isTracking = !isTracking;

                // mengatur warna background
                // tombol berdasarkan status aktif
                // tracking maupun tidak
                trackingUserRoute.setBackground(
                        isTracking ? ContextCompat.getDrawable(context, R.drawable.rounded_shape) :
                                ContextCompat.getDrawable(context, R.drawable.rounded_shape_white)
                );
            }
        });

        // inisialisasi tombol arahkan ke lokasi user
        moveToUserLocation = findViewById(R.id.current_user_location_image);

        // pada saat diklik maka akan
        moveToUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // jika kordinat user tidak null
                if (userCoordinate != null) {

                    // arahkan kamera ke lokasi user
                    mapView.getCamera().setTarget(userCoordinate);

                    // setting tingkatan zoom
                    mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                }
            }
        });

        // inisialisasi layout tutorial
        // dan pada saat tombol tutp di klik maka
        tutorialLayout = new TutorialLayout(context, TUTOR_ROUTE, findViewById(R.id.tutorial_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // layout akan disembunyikan
                tutorialLayout.hide();
            }
        });

        // atur gambar ilustrasi
        tutorialLayout.setImage(R.drawable.tutorial_routing);

        // atur tulisan untuk tutorial
        tutorialLayout.setMessage(context.getString(R.string.tutorial_route));

        // inisialisasi layout loading
        loadingLayout = new LoadingLayout(context,findViewById(R.id.loading_layout));

        // tampilkan pesan loading
        loadingLayout.setMessage(context.getString(R.string.init_here_map));

        // inisialisasi layout error
        // dan pada saat tombol coba lagi ditekan maka
        errorLayout = new ErrorLayout(context, findViewById(R.id.error_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // restart aktivity
                startActivity(getIntent());

                // hancurkan aktivity
                finish();
            }
        });

        // tampilkan pesan error
        errorLayout.setMessage(context.getString(R.string.error_common));

        // memanggil fungsi tampilkan map
        loadMapScene();

        // check jika koneksi internet tidak aktif
        if (!isInternetConnected(context)) {

            // tampilkan layout error
            errorLayout.show();

            // tampilkan dialog
            // tidak ada akses internet
            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) { }
            }).show();
        }
    }


    // fungsi untuk menampilkan map
    private void loadMapScene() {

        // coba
        try {

            // inisialisasi mesin rute
            routingEngine = new RoutingEngine();

            // jika ada yg salah
            // hiraukan
        } catch (InstantiationErrorException ignore) { }

        // menampilkan tampilan map
        // dan pada saat tampil maka
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {

                // check jika tidak ada error
                if (errorCode == null) {

                    // gunakan lokasi akakom
                    mapView.getCamera().setTarget(new GeoCoordinates(-7.792810, 110.408499));
                    mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                }

                // tambahkan map marker untuk lokasi tujuan
                mapView.getMapScene().addMapMarker(createRestaurantMarker(context,destinationRestaurant));

                // panggil fungsi untuk mengatur
                // interaksi user dengan map view
                setTapGestureHandler();

                // panggil fungsi untuk inisialisasi lokasi manajer
                setLocationManager();
            }
        });
    }

    // fungsi untuk mengatur gestur interaksi user di map
    private void setTapGestureHandler() {

        // non aktifkan gestur double tap karna tidak dipakai
        mapView.getGestures().disableDefaultAction(GestureType.DOUBLE_TAP);

        // non aktifkan gestur double finger tap karna tidak dipakai
        mapView.getGestures().disableDefaultAction(GestureType.TWO_FINGER_TAP);

        // non aktifkan gestur double fingger pan karna tidak dipakai
        mapView.getGestures().disableDefaultAction(GestureType.TWO_FINGER_PAN);

        // fungsi untuk menon aktifkan rotasi kamera
        mapView.getCamera().addObserver(new CameraObserver() {

            // pada saat status kamera berubah
            @Override
            public void onCameraUpdated(@NonNull CameraUpdate cameraUpdate) {

                // check, jika bearingnya bukan 0
                // atau menghadap keutara maka
                if (cameraUpdate.bearing != 0) {

                    // paksakan menghadap keutara
                    mapView.getCamera().setBearing(0);
                }
            }
        });
    }

    // fungsi untuk inisialisasi
    // lokasi manajer agar dapat menggunakan
    // service GPS di perangkat user
    @SuppressLint("MissingPermission")
    private void setLocationManager(){

        // check apakah gps diaktifkan
        // jika tidak maka
        if (!isGpsIson(context)){

            // jika tidak, tampilkan pesan error
            errorLayout.show();

            // dan dialog bahwa gps harus diaktifkan
            new DialogRequestLocation(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) { }
            }).show();

            // stop program
            return;
        }

        // inisilisasi service untuk lokasi manajer
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // jika tidak kosong
        // panggil service
        // untuk mendapatkan lokasi user
        if (locationManager != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_REFRESH_TIME,LOCATION_REFRESH_DISTANCE, new LocationListener() {

                // pada saat mendapatkan lokasi user
                @Override
                public void onLocationChanged(Location location) {

                    if (!isUpdateRoute){
                        return;
                    }

                    // jika koordinat user belun diisi
                    userCoordinate = new GeoCoordinates(location.getLatitude(),location.getLongitude());

                    // memanggil fungsi untuk menampilkan rute
                    showRouting(new Waypoint(new GeoCoordinates(userCoordinate.latitude,userCoordinate.longitude)),
                            new Waypoint(new GeoCoordinates(destinationRestaurant.Latitude,destinationRestaurant.Longitude))
                    );

                    if (isTracking){
                        mapView.getCamera().setTarget(userCoordinate);
                    }

                    // coba
                    try {

                        // jika marker user tidak null
                        if (userMarker != null){

                            // hapus marker user
                            mapView.getMapScene().removeMapMarker(userMarker);

                            // kosongkan marker user
                            userMarker = null;
                        }

                        // inisialisasi marker user
                        userMarker = createUserMarker(context,userCoordinate);

                        // tambahkan marker user
                        // ke mapview
                        mapView.getMapScene().addMapMarker(userMarker);

                        // jika terjadi exception
                        // di hiraukan aja
                    }catch (NullPointerException ignore){}
                }

                // untuk fungsi lokasi berubah
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                // untuk fungsi jika provider di aktifkan
                @Override
                public void onProviderEnabled(String provider) {

                }

                // untuk fungsi jika provider di non-aktifkan
                @Override
                public void onProviderDisabled(String provider) {

                }
            },null);


    }


    // fungsi untuk menampilkan rute
    private void showRouting(Waypoint startWaypoint, Waypoint destinationWaypoint){

        // tampilkan alyout loading
        loadingLayout.setMessage(context.getString(R.string.init_routing_map));

        // cek jika mesin rute kosong
        if (routingEngine == null) {

            // stop
            return;
        }

        // buat variabel aray untuk waypoint
        // dengan lokasi user dan tujuan sebagai isi
        List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

        // memanggil fungsi kalulasi
        routingEngine.calculateRoute(
                waypoints,
                new RoutingEngine.CarOptions(),
                new CalculateRouteCallback() {

                    // pada saat berhasil dikalkulasi
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {

                        // sembunyikan layout loading
                        loadingLayout.hide();

                        // hilangkan rute saat ini
                        removeCurrentRoute();

                        // jika tidak ada error dan
                        // rute tersedia tidak null dan
                        // data index pertama tidak null
                        if (routingError == null && routes != null && routes.get(0) != null) {

                            // kalkulasi menit
                            long minute = routes.get(0).getDurationInSeconds() / 60;

                            // kalkulasi jam
                            long hour = minute / 60;

                            // kalkulasi jarak meter
                            int meter = routes.get(0).getLengthInMeters();

                            // ubah ke kilometer
                            int km = meter / 1000;

                            // jika kurang dari 0 maka
                            // meter dan lainya km
                            String dis = km > 0 ? " (" + km + " Km)" : " (" + meter + " M)";

                            // tampilkan waktu durasi rute
                            durration.setText(
                                    hour > 0 ?
                                    hour + " "+ context.getString(R.string.hour) + " " + minute + " "+ context.getString(R.string.minute) + dis :
                                    minute + " "+ context.getString(R.string.minute) + dis
                            );

                            // coba
                            try {

                                // buat geo poline dari route posisi index pertama
                                GeoPolyline routeGeoPolyline = new GeoPolyline(routes.get(0).getPolyline());

                                // buat poli style
                                MapPolylineStyle mapPolylineStyle = new MapPolylineStyle();

                                // atur warna rute
                                mapPolylineStyle.setColor(ContextCompat.getColor(context,R.color.colorPrimaryLight), PixelFormat.ARGB_8888);

                                // lebar rute
                                mapPolylineStyle.setWidth(10);

                                // inisialisasi marker rute
                                routeMapPolyline = new MapPolyline(routeGeoPolyline, mapPolylineStyle);

                                // tampilkan di map
                                mapView.getMapScene().addMapPolyline(routeMapPolyline);

                                // jika terjadi exception
                                // hiraukan
                            } catch (InstantiationErrorException ignore) { }

                        }
                    }
                });
    }

    // fungsi menghilangkan rute saat ini
    private void removeCurrentRoute(){

        // coba
        try {

            // jika mapview dan rute marker tidak kosong
            if (mapView != null && routeMapPolyline != null){

                // hilangkan rute
                mapView.getMapScene().removeMapPolyline(routeMapPolyline);

                // set rute marker ke null
                routeMapPolyline = null;
            }

            // jika terjadi exception
            // hiraukan
        } catch (NullPointerException ignore){}
    }


    // fungsi yg akan dipanggil saat
    // activity dihancurkan
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // set kondisi update route
        // ke false agar tidak perluh mengupdate route
        // lagi saat aktivity dihancurkan
        isUpdateRoute = false;

        // memanggil fungsi destroy di map view
        mapView.onDestroy();

        // memanggil fungsi unsubscribe
        presenter.unsubscribe();
    }

    // fungsi yg akan dipanggil saat
    // activity di pause
    @Override
    protected void onPause() {
        super.onPause();

        // memanggil fungsi pause di map view
        mapView.onPause();
    }

    // fungsi yg akan dipanggil saat
    // activity dilanjutkan
    @Override
    protected void onResume() {
        super.onResume();

        // memanggil fungsi dilanjutkan di map view
        mapView.onResume();
    }


    // fungsi yg akan dipanggil saat
    // user menekan tombol kembali
    @Override
    public void onBackPressed() {

        // set kondisi update route
        // ke false agar tidak perluh mengupdate route
        // lagi saat aktivity dihancurkan
        isUpdateRoute = false;

        // check kode
        switch (backTo){

            // apakah code untuk
            // kembali ke actity splash
            case BACK_TO_SPLASH_ACTIVITY:

                // buka activity
                startActivity(new Intent(context, SplashActivity.class));
                break;

            // apakah code untuk
            // kembali ke actity jelajah
            case BACK_TO_EXPLORE_ACTIVITY:

                // buka activity
                startActivity(new Intent(context, ExploreActivity.class));
                break;

            // apakah code untuk
            // kembali ke actity pencarian
            case BACK_TO_SEARCH_ACTIVITY:

                // buka activity
                startActivity(new Intent(context, SearchRestaurantActivity.class));
                break;

            // apakah code untuk
            // kembali ke actity detail
            case BACK_TO_DETAIL_ACTIVITY:

                // buka activity
                startActivity(new Intent(context, DetailRestaurantActivity.class));
                break;
        }
        // hancurkan activty
        finish();
    }

    // fungsi inject dependensi
    private void injectDependency(){

        // mengdeklarasi variabel component
        ActivityComponent listcomponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();

        // memanggil fungsi inject
        listcomponent.inject(this);
    }
}
