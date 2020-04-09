package com.dimas.rumahmakan.ui.activity.detailRestaurantActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dimas.rumahmakan.BuildConfig;
import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.di.component.ActivityComponent;
import com.dimas.rumahmakan.di.component.DaggerActivityComponent;
import com.dimas.rumahmakan.di.module.ActivityModule;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivity;
import com.dimas.rumahmakan.ui.activity.routingActivity.RoutingActivity;
import com.dimas.rumahmakan.ui.activity.searchRestaurantActivity.SearchRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.splashActivity.SplashActivity;
import com.dimas.rumahmakan.ui.dialog.DialogNoInternet;
import com.dimas.rumahmakan.ui.util.ErrorLayout;
import com.dimas.rumahmakan.ui.util.LoadingLayout;
import com.dimas.rumahmakan.util.Unit;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_DETAIL_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_EXPLORE_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SEARCH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SPLASH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.NO_NEED_TO_BACK;


// ini adalah activity yg digunakan untuk
// menampilkan data detail wisata kuliner
public class DetailRestaurantActivity extends AppCompatActivity implements  DetailRestaurantActivityContract.View {

    // deklarasi injeksi presenter
    @Inject
    public DetailRestaurantActivityContract.Presenter presenter;

    // deklarasi konteks
    private Context context;

    // deklarasi intent
    private Intent intent;

    // deklarasi code
    // untuk kembali ke activity
    // sebelumnya
    private int backTo;

    // deklarasi image untuk wisata kuliner
    private ImageView imageRestaurant;

    // deklarasi text nama wisata kuliner
    private TextView restaurantName;

    // deklarasi text alamat wisata kuliner
    private TextView restaurantAddress;

    // deklarasi text detail info wisata kuliner
    private TextView restaurantDetail;

    // deklarasi tombol kembali
    private ImageView backButton;

    // deklarasi tombol untuk menunjukan route
    private LinearLayout showRoute;

    // deklarasi tombol untuk menunjukan menu makanan
    private LinearLayout showMenu;

    // deklarasi data detail info
    // wisata kuliner
    private RestaurantModel restaurantModel;

    // deklarasi layout untuk
    // menampilkan tampilan loading
    private LoadingLayout loadingDetailData;

    // deklarasi layout untuk
    // menampilkan tampilan error
    private ErrorLayout errorLayout;


    // ini adalah fungsi yg akan
    // pertama kali dijalankan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set view yg digunakan
        setContentView(R.layout.activity_detail_restaurant);

        // memanggil fungsi inisiasi
        initWidget();
    }

    // fungsi inisiasi
    private void initWidget(){

        // inisialisasi konteks
        context = this;

        // inisialisasi intent
        intent = getIntent();

        // inisialisasi data info
        // wisata kuliner
        // yg didapat dari intent
        restaurantModel = (RestaurantModel) intent.getSerializableExtra("data");

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

        // inisialisasi tampilan layout loading
        loadingDetailData = new LoadingLayout(context,findViewById(R.id.loading_layout));

        // menampilkan pesan memeinta detail data kuliner
        loadingDetailData.setMessage(context.getString(R.string.request_data));

        // inisialisasi tampilan error
        // dan saat tombol coba lagi di clik
        errorLayout = new ErrorLayout(context, findViewById(R.id.error_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // restart activity
                startActivity(getIntent());

                // hancurkan activity
                finish();
            }
        });

        // tampilkan pesan error
        errorLayout.setMessage(context.getString(R.string.error_common));

        // inisialisasi gambar untuk wisata kuliner
        imageRestaurant = findViewById(R.id.restaurant_image);

        // inisialisasi text nama
        restaurantName = findViewById(R.id.restaurant_name);

        // untuk saat ini diisi kosong
        restaurantName.setText("");

        // inisialisasi text alamat
        restaurantAddress = findViewById(R.id.restaurant_address);

        // untuk saat ini diisi kosong
        restaurantAddress.setText("");

        // inisialisasi text detail info
        restaurantDetail = findViewById(R.id.detail_text);

        // untuk saat ini diisi kosong
        restaurantDetail.setText("");

        // inisialisasi tombol kembali
        backButton = findViewById(R.id.back_image);

        // saat ditekan maka akan
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // akan memanggil fungsi
                // default tombol kembali
                onBackPressed();
            }
        });

        // inisialisasi tombol tampilkan rute
        showRoute = findViewById(R.id.show_route);

        // pada saat ditekan
        showRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // membuat intent untuk ke activity
                // routing
                Intent i = new Intent(context, RoutingActivity.class);

                // dengan data info wisata kuliner
                i.putExtra("data",restaurantModel);

                // dan code untuk kembali adalah
                // tidak perluh kembali ke activity ini
                i.putExtra("back_to",NO_NEED_TO_BACK);

                // tampilkan activity
                startActivity(i);
            }
        });

        // inisialisasi tombol menu
        showMenu = findViewById(R.id.show_menu);

        // saat ditekan maka
        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // membuat intent untuk browsing
                // menggunakan browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantModel.UrlMenu));

                // tampilkan activity
                startActivity(browserIntent);
            }
        });


        // memanggil fungsi
        // untuk mendapatkan info wisata kuliner
        getOneRestaurant();
    }

    // fungsi untuk mendapatkan info wisata kuliner
    private void getOneRestaurant(){

        // memanggil fungsi di presenter untuk mendapatkan info wisata kuliner
        presenter.getOneRestaurant(restaurantModel.Id,true);
    }

    // fungsi ini dijalankan
    // saat response didapat dari server
    @Override
    public void onGetOneRestaurant(@Nullable RestaurantModel r) {

        // jika data yg didapat tidak null
        // maka
        if (r != null){

            // membuat intance picasso
            // untuk meletakan gambar ke
            // image wisata kuliner
            Picasso.get()
                    .load(r.UrlImage)
                    .into(imageRestaurant);

            // tampilkan teks nama wisata kuliner
            restaurantName.setText(r.Name);

            // tampilkan teks alamat wisata kuliner
            restaurantAddress.setText(r.Address);

            // tampilkan teks deskripsi wisata kuliner
            restaurantDetail.setText(r.Description);

            // ganti value info wisata kuliner
            restaurantModel = r;
        }
    }

    // ------------ //

    // ini adalah fungsi yg digunakan untuk menentukan
    // kapan tampilan loading akan ditampilkan
    @Override
    public void showProgressOneRestaurant(Boolean show) {
        loadingDetailData.setVisibility(show);
    }

    // ini adalah fungsi yg digunakan untuk menentukan
    // kapan tampilan error akan ditampilkan
    @Override
    public void showErrorOneRestaurant(String error) {
        if (BuildConfig.DEBUG){
            errorLayout.setMessage(error);
        }

        // tampilkan pesan error
        errorLayout.show();
    }

    // ------------ //

    // fungsi yg akan dipanggil saat
    // activity dihancurkan
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // memanggil fungsi unsubscribe
        presenter.unsubscribe();
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

        // hancurkan activty
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
