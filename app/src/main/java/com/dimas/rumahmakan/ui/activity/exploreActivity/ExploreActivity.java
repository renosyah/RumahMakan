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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

// ini adalah activity yg digunakan untuk
// menampilkan data wisata kuliner yang
// ada disekitar wilayah user
public class ExploreActivity extends AppCompatActivity implements ExploreActivityContract.View{

    // presenter yg akan diinject
    @Inject
    public ExploreActivityContract.Presenter presenter;

    // deklarasi konteks
    private Context context;

    // deklarasi map view
    private MapViewLite mapView;

    // deklarasi form text cari wisata kuliner
    private EditText searchRestaurant;

    // deklarasi tombol cari
    private ImageView goSearch;

    // deklarasi untuk menampilkan list wisata kuliner
    // dalam bentuk stack view
    private SwipeStack restaurantStackView;

    // deklarasi dan inisialisasi array data wisata kuliner
    private ArrayList<RestaurantModel> restaurantModels = new ArrayList<>();

    // deklarasi dan inisialisasi array data marker wisata kuliner
    private ArrayList<MapMarker> restaurantMarker = new ArrayList<>();

    // deklarasi adapter yg akan dipakai stack view
    private AdapterSwipeStackRestaurant adapterSwipeStackRestaurant;

    // variabel offset dan limit
    // untuk pagination
    // namun saat ini belum digunakan
    private int offset = 0,limit = 10;

    // variabel untuk mencari data
    // namun saat ini belum digunakan
    private String searchBy = "nama",searchValue = "";

    // variabel radius yg dipakai
    // untuk menentukan batas maksimal
    // lokasi wisata kuliner terdekat
    private double range = BuildConfig.MIN_RADIUS;

    // deklarasi tombol untuk
    // menampilkan kembali setelah selesai swipe
    // dan stack dalam keadaan kosong
    private Button reloadButton;

    // deklarasi service lokasi manager
    private LocationManager locationManager;

    // deklarasi user lokasi
    private GeoCoordinates userCoordinate;

    // deklarasi user marker
    // yg akan dipakai di map
    private MapMarker userMarker;

    // deklarasi layout tutorial
    private TutorialLayout tutorialLayout;

    // deklarasi layout loading
    private LoadingLayout loadingMessageLayout;

    // deklarasi layout error
    private ErrorLayout errorMessageLayout;

    // ini adalah fungsi yg akan dipanggil
    // pertama kali saat activity dijalankan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set view yg digunakan
        setContentView(R.layout.activity_explore);

        // memanggil fungsi inisiasi
        initWidget(savedInstanceState);
    }

    // fungsi inisialisasi
    private void initWidget(Bundle savedInstanceState){

        // inisialisasi konteks
        context = this;

        // memanngil fungsi inject dependensi
        injectDependency();

        // memanggil fungsi inject activity
        presenter.attach(this);

        // memanngil fungsi subscribe
        presenter.subscribe();

        // inisialisasi map view
        mapView = findViewById(R.id.map_view);

        // panggil fungsi on create mapview
        mapView.onCreate(savedInstanceState);

        // inisialisasi tampilan layout tutorial
        // yg mana saat diklik tombol close maka
        tutorialLayout = new TutorialLayout(context, StaticVariabel.TUTOR_SWIPE, findViewById(R.id.tutorial_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // tutorial layout
                // akan disembunyikan
                tutorialLayout.hide();
            }
        });

        // tampilkan pesan tutorial
        tutorialLayout.setMessage(context.getString(R.string.tutorial_swipe));

        // tampilkan gambar ilustrasi
        tutorialLayout.setImage(R.drawable.tutorial_swipe_restaurant);

        // inisialisasi layout loading
        loadingMessageLayout = new LoadingLayout(context,findViewById(R.id.loading_layout));

        // tampilkan pesan loading
        loadingMessageLayout.setMessage(context.getString(R.string.init_here_map));

        // inisialisasi tampilan error
        // dan saat tombol coba lagi di clik
        errorMessageLayout = new ErrorLayout(context, findViewById(R.id.error_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // restart activity
                startActivity(getIntent());

                // hancurkan activity
                finish();
            }
        });

        // tampilkan pesan error
        errorMessageLayout.setMessage(context.getString(R.string.error_common));

        // inisialisasi form text pencarian
        // wisata kuliner
        searchRestaurant = findViewById(R.id.search_restaurant_edittext);

        // tambahkan kondisi saat text berubah
        searchRestaurant.addTextChangedListener(new TextWatcher() {

            // sebelum text berubah
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // saat text berubah
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // hanya menampilkan tombol
                // mulai cari saat text tidak kosong
                goSearch.setVisibility(searchRestaurant.getText().toString().trim().isEmpty() ? View.INVISIBLE : View.VISIBLE);
            }
        });

        // pada saat tombol cari di tekan pada keyboard
        searchRestaurant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // check lagi apakah benar tombol cari pada keyboard
                // yg ditekan, jika iya maka
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    // tombol cari akan dijalankan fungsi perform clicknya
                    goSearch.performClick();

                    // balikkan nilai true
                    return true;
                }

                // balikkan nilai false
                return false;
            }
        });

        // inisialisasi tombol
        // mulai cari lalu
        goSearch = findViewById(R.id.go_search);

        // setting tipe visibilitas jadi
        // tidak terlihat
        goSearch.setVisibility(View.INVISIBLE);

        // pada saat diklik maka
        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check lagi jika text pencarian
                // kosong maka program akan dihentikan
                if (searchRestaurant.getText().toString().trim().isEmpty()){
                    return;
                }

                // buat intent untuk ke activity
                // pencarian wisata kuliner
                // untuk menampilkan hasil
                Intent i = new Intent(context, SearchRestaurantActivity.class);

                // bawa data kata kunci
                i.putExtra("data",searchRestaurant.getText().toString());

                // bawa kode activity akan kembali ke explore
                // jika sudah selesai
                i.putExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);

                // mulai aktivity
                startActivity(i);

                // hancurkan activity ini
                finish();
            }
        });

        // inisialisasi tombol untuk
        // refresh stack view
        reloadButton = findViewById(R.id.button_reload);

        // pada saat diklik
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // panggil fungsi untuk menghilangkan marker
                removeRestaurantMarker();

                // bersihkan array marker
                restaurantMarker.clear();

                // bersihkan array restaurant
                restaurantModels.clear();

                // notifkasi stackview
                // bahwa data di adapter berubah
                adapterSwipeStackRestaurant.notifyDataSetChanged();

                // panggil fungsi untuk
                // request data wisata kuliner terdekat
                getAllNearestRestaurant(userCoordinate,false);

                // balikkan visibilitas
                // tombol reload ke hilang
                reloadButton.setVisibility(View.GONE);

                // balikkan visibilitas
                // stack view agar terlihat
                restaurantStackView.setVisibility(View.VISIBLE);
            }
        });

        // inisialisasi adalpter
        // untuk stack view
        // dan pada saat totmbol detail dotekan maka
        adapterSwipeStackRestaurant = new AdapterSwipeStackRestaurant(context, restaurantModels, new Unit<RestaurantModel>() {
            @Override
            public void invoke(RestaurantModel o) {

                // membuat intent untuk ke detail
                Intent i = new Intent(context, DetailRestaurantActivity.class);

                // bawa data wisata kuliner yg dipilih
                i.putExtra("data",o);

                // bawa kode untuk kembali ke activity ini
                i.putExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);

                // mulai aktivity
                startActivity(i);

                // hancurkan aktivity ini
                finish();
            }
        });

        // inisialisasi stack view
        // untuk menampilkan data wisata
        // kuliner dalam bentuk stack view
        restaurantStackView = findViewById(R.id.restaurant_swipe_stack);

        // isi adapter untuk stackview
        restaurantStackView.setAdapter(adapterSwipeStackRestaurant);

        // setting kondisi pada saat user
        // berinteraksi dengan stack view
        restaurantStackView.setListener(new SwipeStack.SwipeStackListener() {

            // jika user melakukan swipe ke kiri
            @Override
            public void onViewSwipedToLeft(int position) {

                // panggil saja fungsi swipe kekanan
                this.onViewSwipedToRight(position);
            }

            // jika user memlakukan swipe ke kanan
            @Override
            public void onViewSwipedToRight(int position) {

                // check lagi jika array data
                // tidak kurang dari posisi ditambah dengan 1
                // untuk menghindari indek out of bound
                if (restaurantModels.size() <= position + 1){
                    return;
                }

                // dapatkan data pada array di posisi
                // dengan ditambah dengan 1
                RestaurantModel r = restaurantModels.get(position + 1);

                // menuju ke arrah marker
                // atau lokasi wisata kuliner
                mapView.getCamera().setTarget(new GeoCoordinates(r.Latitude,r.Longitude));

                // isi kamera zoom level
                mapView.getCamera().setZoomLevel(ZOOM_LEVEL);

                // konfigurasi jangkar untuk marker
                // agar tampil setidaknya diatas dari titik tengah
                // kamera, karna tampilan di bagian bawah
                // telah habis diambil oleh stackview
                mapView.getCamera().setTargetAnchorPoint(new Anchor2D(0.5F, 0.3F));
            }

            // pada saat stackview
            // dalam keadaan kosong
            @Override
            public void onStackEmpty() {

                // tampilkan tombol reload
                reloadButton.setVisibility(View.VISIBLE);

                // sembunyikan stackview
                restaurantStackView.setVisibility(View.GONE);
            }
        });

        // sembunyikan tombol reload
        reloadButton.setVisibility(View.GONE);

        // tampilkan stackview
        restaurantStackView.setVisibility(View.VISIBLE);

        // sebelum menampilkan map
        // minta izin terlebih dahulu
        // di perangkat user
        requestLocationPermission(new Unit<Boolean>() {

            // jika izin telah disetujui
            @Override
            public void invoke(Boolean o) {

                // tampilkan mapview
                loadMapScene();
            }
        });


        // check apakah koneksi internet
        // device dapat digunakan
        if (!isInternetConnected(context)){

            // tampilkan layout error
            errorMessageLayout.show();

            // tampilkan dialog
            // tidak ada koneksi intenet
            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) { }
            }).show();
        }
    }

    // fungsi untuk mendapatkan data
    // lokasi wisata kuliner terdekat
    private void getAllNearestRestaurant(GeoCoordinates userCoordinate, boolean loading){

        // tampilkan pesan loading
        loadingMessageLayout.setMessage(context.getString(R.string.finding_nearest_restaurant));

        // panggil fungsi presenter
        presenter.getAllNearestRestaurant(
                userCoordinate.latitude,userCoordinate.longitude,range,searchBy,searchValue,offset,limit,loading
        );
    }

    // fungsi untuk menampilkan map view
    private void loadMapScene() {

        // check kondisi pada saat map view berhasil diload
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {

                // jika tidak ada error
                if (errorCode == null) {

                    // gunakan lokasi akakom
                    // sebagai target default
                    mapView.getCamera().setTarget(new GeoCoordinates(-7.792810, 110.408499));
                    mapView.getCamera().setZoomLevel(ZOOM_LEVEL);
                    mapView.getCamera().setTargetAnchorPoint(new Anchor2D(0.5F, 0.3F));
                }

                // panggil fungsi lokasi manajer
                setLocationManager();

                // panggil fungsi set gestur
                // dimapview
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
            errorMessageLayout.show();

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
                        // lokasi wisata kuliner terdekat
                        getAllNearestRestaurant(new GeoCoordinates(location.getLatitude(),location.getLongitude()),true);
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


    // ------------ //

    // ini adalah fungsi yg dipanggil saat response
    // telah berhasil didapat saat request
    @Override
    public void onGetAllNearestRestaurant(@Nullable ArrayList<RestaurantModel> all) {

        // jika tidak null
        if (all != null){

            // untuk setiap data array di response
            for (RestaurantModel r : all){

                // akan dipanggil fungsi untuk
                // menunjukan jarak
                r.Distance = r.calculateDistance(userCoordinate);

                // memanggil fungsi untuk membuat marker lokasi
                MapMarker m = createRestaurantMarker(context,r);

                // tambahkan ke array marker
                restaurantMarker.add(m);

                // tampilkan marker dimap
                mapView.getMapScene().addMapMarker(m);
            }

            // tambahkan semua data response ke array
            // data wisata kuliner
            restaurantModels.addAll(all);
        }

        // jika array tidak kosong
        if (restaurantModels.size() > 0) {

            // arahkan kamera ke marker
            // data lokasi wisata kuliner
            // posisi pertama
            mapView.getCamera().setTarget(new GeoCoordinates(restaurantModels.get(0).Latitude,restaurantModels.get(0).Longitude));

            // setting tingkatan zoom kamera
            mapView.getCamera().setZoomLevel(ZOOM_LEVEL);

            // konfigurasi jangkar untuk marker
            // agar tampil setidaknya diatas dari titik tengah
            // kamera, karna tampilan di bagian bawah
            // telah habis diambil oleh stackview
            mapView.getCamera().setTargetAnchorPoint(new Anchor2D(0.5F, 0.3F));
        }

        // beritahu stackview bahwa data
        // pada adapter telah berubah
        adapterSwipeStackRestaurant.notifyDataSetChanged();
    }

    // ini adalah fungsi yg digunakan untuk menentukan
    // kapan tampilan loading akan ditampilkan
    @Override
    public void showProgressAllNearestRestaurant(Boolean show) {
        loadingMessageLayout.setVisibility(show);
    }


    // ini adalah fungsi yg digunakan untuk menentukan
    // kapan tampilan error akan ditampilkan
    @Override
    public void showErrorAllNearestRestaurant(String error) {
        if (BuildConfig.DEBUG){
            errorMessageLayout.setMessage(error);
        }

        // tampilkan pesan error
        errorMessageLayout.show();
    }

    // ------------ //

    // fungsi untuk menghilangkan marker
    private void removeRestaurantMarker(){

        // untuk setiap marker di array
        for (MapMarker m : restaurantMarker){

            // hilangkan marker pada map
            mapView.getMapScene().removeMapMarker(m);
        }
    }

    // fungsi untuk mengatur gestur interaksi user di map
    private void setTapGestureHandler() {

        // atur gestur pada map view
        // untuk interaksi tap
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(@NotNull Point2D touchPoint) {

                // untuk saat ini user pasti akan
                // mengklik marker
                // panggil fungsi saat marker di tap
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

                // jika item kosong
                // stop program
                if (pickMapItemsResult == null) {
                    return;
                }

                // dapatkan marker
                // dari item map
                MapMarker mapMarker = pickMapItemsResult.getTopmostMarker();

                // jika kosong
                // hentikan program
                if (mapMarker == null) {
                    return;
                }

                // dapatkan metadata dari map
                Metadata metadata = mapMarker.getMetadata();

                // jika kosong
                // hentikan program
                if (metadata == null) {
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

                // membuat intent untuk ke detail
                Intent i = new Intent(context, DetailRestaurantActivity.class);

                // bawa data wisata kuliner yg dipilih
                i.putExtra("data",new RestaurantModel(id));

                // mebawa code untuk kembali ke activty ini
                i.putExtra("back_to",BACK_TO_EXPLORE_ACTIVITY);

                // mulai aktivity
                startActivity(i);

                // hancurkan aktivity
                finish();
            }
        });
    }

    // fungsi yg akan memberikan hasil
    // apakah izin diterima atau tidak
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // check kode permission
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION){

            // restart aktivity
            startActivity(new Intent(context,ExploreActivity.class));

            // hancurkan aktivity
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    // fungsi untuk mengambil dialog
    // meminta izin menggunakan layanan
    // gps dari preangkat
    private void requestLocationPermission(Unit<Boolean> doIt){

        // check jika izin belum diberikan
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // tampilkan dialog
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        } else {

            // lanjutkan
            doIt.invoke(true);
        }
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
