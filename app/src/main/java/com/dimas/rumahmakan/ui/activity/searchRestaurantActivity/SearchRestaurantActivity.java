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
import com.dimas.rumahmakan.ui.dialog.DialogAboutApp;
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

// ini adalah aktivity yg digunakan untuk
// menampilkan hasil pencarian wisata kuliner
public class SearchRestaurantActivity extends AppCompatActivity implements SearchRestaurantActivityContract.View {

    // presenter yg akan diinject
    @Inject
    public SearchRestaurantActivityContract.Presenter presenter;

    // deklarasi konteks
    private Context context;

    // deklarasi intent
    private Intent intent;

    // deklarasi kode kembali
    private int backTo;

    // deklarasi text yg dicari
    private TextView searchText;

    // deklarasi tombol text memilih list atau map
    private TextView chooseList,chooseMap;

    // deklarasi tombol kembali
    private ImageView imageBack;

    // deklarasi map view
    private MapViewLite mapView;

    // deklarasi recycleview untuk menampilkan wisata kuliner
    private RecyclerView restaurantRecycleView;

    // data array untuk menampung data wisata kuliner
    private ArrayList<RestaurantModel> restaurantModels = new ArrayList<>();

    // data array untuk menampung data marker wisata kuliner
    private ArrayList<MapMarker> restaurantMarker = new ArrayList<>();

    // deklarasi adapter
    private AdapterRestaurant adapterRestaurant;

    // variabel offset dan limit
    // untuk pagination
    // namun saat ini belum digunakan
    private int offset = 0,limit = 10;

    // variabel untuk mencari data
    // namun saat ini belum digunakan
    private String searchBy = "nama",searchValue = "";

    // variabel untuk mencari data
    // namun saat ini belum digunakan
    private String orderBy = "nama",orderDir = "asc";

    // deklarasi layout untuk menampilkan
    // tampilan bahwa data tidak ditemukan
    private View notFoundLayout;

    // deklarasi layout list
    private View layoutList;

    // deklarasi lokasi manajer
    private LocationManager locationManager;

    // deklarasi data kordinat user
    private GeoCoordinates userCoordinate;

    // deklarasi data marker map untuk user
    private MapMarker userMarker;

    // deklarasi layout untuk loading
    private LoadingLayout loadingLayout;

    // deklarasi layout untuk error
    private ErrorLayout errorLayout;


    // fungsi yg akan dipanggil pertama kali saat aktivity dijalankan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view yg digunakan
        setContentView(R.layout.activity_search_restaurant);

        // memanggil fungsi inisialisasi widget
        initWidget(savedInstanceState);
    }

    // fungsi inisialisasi widget
    private void initWidget(Bundle savedInstanceState){

        // inisialisasi kontek
        context = this;

        // inisialisasi intent
        intent = getIntent();

        // jika intent membawa data maka
        if (intent.hasExtra("data")){

            // inisialisasi value pencarian dari intent
            searchValue = intent.getStringExtra("data");

            // simpan sebagai cache
            new SerializableSave(context, StaticVariabel.LAST_SEARCH).save(new SerializableSave.SimpleCache(searchValue));

            // jika tidak ada
            // keluarkan kata kunci terakhir yg digunakan untuk pencarian
        } else if (new SerializableSave(context, StaticVariabel.LAST_SEARCH).load() != null){

            // inisialisasi value pencarian dari kunci terakhir yg dikeluarkan
            searchValue = ((SerializableSave.SimpleCache) new SerializableSave(context, StaticVariabel.LAST_SEARCH).load()).data;
        }

        // inisialisasi kode untuk kembali ke aktivity
        // sebelumnya
        backTo = intent.getIntExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);

        // memanngil fungsi inject dependensi
        injectDependency();

        // memanggil fungsi inject activity
        presenter.attach(this);

        // memanngil fungsi subscribe
        presenter.subscribe();

        // inisialisasi layout loading
        loadingLayout = new LoadingLayout(context,findViewById(R.id.loading_layout));

        // inisialisasi pesan loading
        loadingLayout.setMessage(context.getString(R.string.init_here_map));

        // inislialisasi layout error
        // dan pada saat tombol diklik
        errorLayout = new ErrorLayout(context, findViewById(R.id.error_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // restar aktivity
                startActivity(getIntent());

                // hancurkan aktivity
                finish();
            }
        });

        // tampilkan pesan error
        errorLayout.setMessage(context.getString(R.string.error_common));

        // inisialisasi map view
        mapView = findViewById(R.id.map_view);

        // panggil fungsi on create mapview
        mapView.onCreate(savedInstanceState);

        // inisialisasi text hasil pencarian
        searchText = findViewById(R.id.search_result_text);

        // tampilkan text
        searchText.setText(context.getString(R.string.search_result_of) + "\"" + searchValue + "\"");

        // inisialisai tombol kembali
        imageBack = findViewById(R.id.image_back);

        // pada saat diklik
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // akan memanggil fungsi
                // default tombol kembali
                onBackPressed();
            }
        });

        // inisialisasi adapter
        // dan pada saat salah satu itemnya diklik
        // maka akan
        adapterRestaurant = new AdapterRestaurant(context, restaurantModels, new Unit<RestaurantModel>() {
            @Override
            public void invoke(RestaurantModel o) {

                // membuat intent untuk ke activity berikutnya
                Intent i = new Intent(context, DetailRestaurantActivity.class);

                // tambahkan data
                i.putExtra("data",o);

                // tambahkan kode kembali
                i.putExtra("back_to",BACK_TO_SEARCH_ACTIVITY);

                // mulai aktivity
                startActivity(i);

                // hancurkan aktivity ini
                finish();
            }
        });

        // inisialisasi recycleview untuk menampilkan
        // list data-data hasil pencarian wisata kuliner
        restaurantRecycleView = findViewById(R.id.restaurant_recycleview);

        // atur adapter yg akan digunakan
        restaurantRecycleView.setAdapter(adapterRestaurant);

        // atur layout manager
        restaurantRecycleView.setLayoutManager(new GridLayoutManager(context,2));

        // tampilkan
        restaurantRecycleView.setVisibility(View.VISIBLE);

        // inisialisasi layout tidak ditemukan
        notFoundLayout = findViewById(R.id.not_found_layout);

        // sembunyikan layout
        notFoundLayout.setVisibility(View.GONE);

        // inisialisasi layout untuk list
        layoutList = findViewById(R.id.layout_list);

        // inisialisasi tombol pada saat list menu dipilih
        chooseList = findViewById(R.id.choose_list_text);

        // pada saat ditekan maka
        chooseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sembunyikan map view
                mapView.setVisibility(View.GONE);

                // tampilkan layout list
                layoutList.setVisibility(View.VISIBLE);

                // ganti wana text untuk tobol choose list
                chooseList.setTextColor(ContextCompat.getColor(context,R.color.textColorWhite));

                // ganti wana background text untuk tombol choose list
                chooseList.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));

                // ganti wana text untuk tobol choose map
                chooseMap.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));

                // ganti wana background text untuk tombol choose map
                chooseMap.setBackgroundColor(ContextCompat.getColor(context,R.color.textColorWhite));

            }
        });

        // inisialisasi tombol choose map
        chooseMap = findViewById(R.id.choose_map_text);

        // pada saat diklik
        chooseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tampilkan map view
                mapView.setVisibility(View.VISIBLE);

                // sembunyikan list
                layoutList.setVisibility(View.GONE);

                // ganti wana text untuk tobol choose list
                chooseList.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));

                // ganti wana text untuk tobol choose list
                chooseList.setBackgroundColor(ContextCompat.getColor(context,R.color.textColorWhite));

                // ganti wana background text untuk tobol choose map
                chooseMap.setTextColor(ContextCompat.getColor(context,R.color.textColorWhite));

                // ganti wana background text untuk tobol choose map
                chooseMap.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryLight));


            }
        });


        // panggil fungsi
        loadMapScene();

        // check apakah koneksi internet
        // device dapat digunakan
        if (!isInternetConnected(context)){

            // tampilkan dialog
            // untuk mengaktifkan
            // koneksi internet
            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) { }
            }).show();
        }

        // pilih map
        chooseMap.performClick();
    }

    // fungsi yg akan digunakan untuk merequest data
    // wisata kuliner berdasarkan kata kunci
    private void getAllRestaurant(GeoCoordinates userCoordinate){

        // tampilkan pesan loading
        loadingLayout.setMessage(context.getString(R.string.finding_restaurant));

        // panggil fungsi dari presenter untuk request data
        presenter.getAllRestaurant(
                searchBy,searchValue,orderBy,orderDir,offset,limit,true
        );
    }


    // fungsi untuk menampilkan map view
    private void loadMapScene() {

        // memanggil fungsi dari mapview untuk menampilkan map
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {

            // pada saat map ditampilkan maka
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {

                // check jika tidak ada error
                if (errorCode == null) {

                    // gunakan lokasi akakom
                    mapView.getCamera().setTarget(new GeoCoordinates(-7.792810, 110.408499));
                    mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                }

                // panggil fungsi inisialisasi lokasi manajer
                setLocationManager();

                // panggil fungsi inisialisasi gestur
                setTapGestureHandler();
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
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {

                // pada saat mendapatkan lokasi user
                @Override
                public void onLocationChanged(Location location) {

                    // jika koordinat user belun diisi
                    if (userCoordinate == null){

                        // panggil fungsi untuk mendaptkan
                        // lokasi wisata kuliner
                        getAllRestaurant(new GeoCoordinates(location.getLatitude(),location.getLongitude()));
                    }

                    // inisialisasi kordinat user
                    userCoordinate = new GeoCoordinates(location.getLatitude(),location.getLongitude());

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

    // fungsi untuk mengecheck apakah data yg didapt kosong
    private void checkIsEmpty(){

        // jika kosong layout not found ditampilkan
        notFoundLayout.setVisibility(!restaurantModels.isEmpty() ? View.GONE : View.VISIBLE);

        // jika kosong layout daftar lokasi wisata kuliner ditampilkan
        restaurantRecycleView.setVisibility(restaurantModels.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ------------ //

    // fungsi yg akan dipanggil saat data didapatkan
    @Override
    public void onGetAllRestaurant(@Nullable ArrayList<RestaurantModel> all) {

        // jika bukan null
        if (all != null){

            // iterasi setiap data dengan for each
            for (RestaurantModel r : all){

                // tentukan jarak
                r.Distance = r.calculateDistance(userCoordinate);

                // buat marker lokasi
                MapMarker m = createRestaurantMarker(context,r);

                // tambahkan marker ke array
                restaurantMarker.add(m);

                // tampilkan marker dipeta
                mapView.getMapScene().addMapMarker(m);
            }

            // tambahkan semua data ke array
            restaurantModels.addAll(all);
        }

        // jika data tidak kosong
        if (restaurantModels.size() > 0) {

            // ambil data pertama dan arrahkan kamera ke markernya
            mapView.getCamera().setTarget(new GeoCoordinates(restaurantModels.get(0).Latitude,restaurantModels.get(0).Longitude));

            // atur tingkatan zoom
            mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
        }

        // beritahu adapter jika data array berubah
        adapterRestaurant.notifyDataSetChanged();

        // check lagi jika data array kosong
        checkIsEmpty();
    }

    // fungsi yg dipanggil dan akan menampilkan progress
    @Override
    public void showProgressAllRestaurant(Boolean show) {

        // tampilkan layot loading
        loadingLayout.setVisibility(show);
    }

    // fungsi yg akan menampilkan error
    @Override
    public void showErrorAllRestaurant(String error) {
        if (BuildConfig.DEBUG){
            errorLayout.setMessage(error);
        }

        // tampilkan layout error
        errorLayout.show();
    }

    // ------------ //

    // fungsi untuk mengatur gestur interaksi user di map
    private void setTapGestureHandler() {

        // atur gestur pada map view
        // untuk interaksi tap
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(@NotNull Point2D touchPoint) {

                // untuk saat ini user pasti akan
                // mengklik marker
                // panggil fungsi saat marker di ta
                pickMapMarker(touchPoint);
            }
        });

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

    // fungsi yg digunakan untuk mengambil data
    // dari marker yg diklik
    private void pickMapMarker(final Point2D touchPoint) {

        // tentukan maksimum pixel adalah 2 pixel
        float radiusInPixel = 2;

        // saat map item diklik
        mapView.pickMapItems(touchPoint, radiusInPixel, new PickMapItemsCallback() {

            // yg dipanggil saat map
            // diklik
            @Override
            public void onMapItemsPicked(@Nullable PickMapItemsResult pickMapItemsResult) {

                // jika item kosong maka
                if (pickMapItemsResult == null) {


                    return;
                }

                // dapatkan marker
                // dari item map
                MapMarker mapMarker = pickMapItemsResult.getTopmostMarker();

                // jika kosong maka
                if (mapMarker == null) {

                    // stop program
                    return;
                }

                // dapatkan metadata dari map
                Metadata metadata = mapMarker.getMetadata();

                // jika kosong maka
                if (metadata == null) {
                    // stop program
                    return;
                }

                // dapatkan id dari metadata
                int id = metadata.getInteger("id");

                // jika -1 artinya itu marker user
                if (id == -1){

                    // tampilkan dialog tentang aplikasi
                    new DialogAboutApp(context, new Unit<Boolean>() {
                        @Override
                        public void invoke(Boolean o) { }
                    }).show();
                    return;
                }

                // buat instance sederhana dari lokasi wisata kuliner
                RestaurantModel r = new RestaurantModel(
                        id,
                        metadata.getString("name"),
                        metadata.getString("address"),
                        metadata.getDouble("distance")
                );

                // tampilkan dialog detail lokasi wisata kuliner
                // jika tombolnya diklik maka
                new DialogDetailRestaurant(context,r, new Unit<Boolean>() {
                            @Override
                            public void invoke(Boolean o) {

                                // membuat intent untuk ke detail
                                Intent i = new Intent(context, DetailRestaurantActivity.class);

                                // bawa data wisata kuliner yg dipilih
                                i.putExtra("data",r);

                                // mebawa code untuk kembali ke activty ini
                                i.putExtra("back_to",BACK_TO_SEARCH_ACTIVITY);

                                // mulai aktivity
                                startActivity(i);

                                // hancurkan aktivity ini
                                finish();
                            }
                        }
                ).show();

            }
        });
    }

    // fungsi yg akan dipanggil saat
    // activity dihancurkan
    @Override
    protected void onDestroy() {
        super.onDestroy();

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
                startActivity(new Intent(context,ExploreActivity.class));
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

        // hancurkan aktivity
        finish();
    }

    // fungsi inject dependensi
    private void injectDependency(){

        // menginisialisasi variabel component
        ActivityComponent listcomponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();

        // memanggil fungsi inject
        listcomponent.inject(this);
    }

}
