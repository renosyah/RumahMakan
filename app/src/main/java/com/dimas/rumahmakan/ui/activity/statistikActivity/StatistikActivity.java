package com.dimas.rumahmakan.ui.activity.statistikActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.dimas.rumahmakan.BuildConfig;
import com.dimas.rumahmakan.R;
import com.dimas.rumahmakan.di.component.ActivityComponent;
import com.dimas.rumahmakan.di.component.DaggerActivityComponent;
import com.dimas.rumahmakan.di.module.ActivityModule;
import com.dimas.rumahmakan.model.cityModel.CityModel;
import com.dimas.rumahmakan.ui.adapter.AdapterCityModel;
import com.dimas.rumahmakan.ui.dialog.DialogNoInternet;
import com.dimas.rumahmakan.ui.util.ErrorLayout;
import com.dimas.rumahmakan.ui.util.LoadingLayout;
import com.dimas.rumahmakan.util.Unit;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.dimas.rumahmakan.util.CheckService.isInternetConnected;

// ini adalah aktivity untuk menampilkan data
// jumlah total wisata kulner dalam bentuk diagram
public class StatistikActivity extends AppCompatActivity implements StatistikActivityContract.View {

    // presenter yg akan diinject
    @Inject
    public StatistikActivityContract.Presenter presenter;

    // deklarasi konteks
    private Context context;

    // deklarasi chart view
    private AnyChartView chart;

    // deklarasi tombol kembali
    private Button back;

    // deklarasi recycle view deskripsi
    private RecyclerView listDescription;

    // deklarasi adapter
    private AdapterCityModel adapterCityModel;

    // deklarasi dan inisialisasi array data kota
    private ArrayList<CityModel> cityModels = new ArrayList<>();

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

    // deklarasi untuk menampilkan layout loading
    private LoadingLayout loadingLayout;

    // deklarasi untuk menampilkan layout error
    private ErrorLayout errorLayout;

    // fungsi yg akan pertama kali dipanggil saat
    // aktivity dibuat
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view yg digunakan
        setContentView(R.layout.activity_statistik);

        // inisialisasi widget
        initWidget();
    }

    // fungsi inisialisasi
    private void initWidget(){

        // inisialisasi konteks
        context = this;

        // memanngil fungsi inject dependensi
        injectDependency();

        // memanggil fungsi inject activity
        presenter.attach(this);

        // memanngil fungsi subscribe
        presenter.subscribe();

        // inisialisasi chart view
        chart = findViewById(R.id.chart);

        // buat agar tidak bisa diklik
        chart.setEnabled(false);

        // pada saat dirender
        chart.setOnRenderedListener(new AnyChartView.OnRenderedListener() {
            @Override
            public void onRendered() {

                // sembunyikan layout loading
                loadingLayout.setVisibility(false);
            }
        });

        // inisialisasi list deskripsi
        listDescription = findViewById(R.id.list_description);

        // inisialisasi adapter
        adapterCityModel = new AdapterCityModel(context,cityModels);

        // atur adapter
        listDescription.setAdapter(adapterCityModel);

        // atur layout manager yg digunakan
        listDescription.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));

        // inisialisasi layout untuk loading
        loadingLayout = new LoadingLayout(context,findViewById(R.id.loading_layout));

        // tampilkan pesan loading
        loadingLayout.setMessage(context.getString(R.string.init_chart));

        // inisialisasi layout untuk error
        // dan pada saat tombol ditekan maka
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

        // inisialisasi tombil kembali
        back = findViewById(R.id.button_back);

        // pada saat ditekan
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hancurkan aktivity
                finish();
            }
        });


        // apakah terkoneksi ke internet
        if (!isInternetConnected(context)) {

            // tampilkan dialog internet harus aktif
            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) { }
            }).show();
        }

        // mememanggil fungsi untuk request
        // data kota beserta jumlah wisata kuliner
        // yg ada di kota tersebut
        getAllCity();
    }

    // fungsi untuk request data kota
    private void getAllCity(){

        // panggil fungsi presenter
        // untuk request data kota
        // beserta jumlah wisata kuliner
        // yg ada di kota tersebut
        presenter.getAllCity(searchBy,searchValue,orderBy,orderDir,offset,limit,true);
    }

    // fungsi untuk menampilkan data statistik
    private void showStatistic(){

        // sembunyikan layout loading
        loadingLayout.setVisibility(true);

        // buat variabel kartesian
        Cartesian cartesian = AnyChart.column();

        // buat data array untuk menampung data
        ArrayList<DataEntry> data = new ArrayList<DataEntry>();

        // iterasi setiap data array pada kota
        for (CityModel c : cityModels){

            // dan tambahkan ke array data
            data.add(new ValueDataEntry(c.Name,c.Total));
        }

        // buat variabel kolom
        Column column = cartesian.column(data);

        // atur atribut kolom
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        // atur animasi yg digunakan ke true
        cartesian.animation(true);

        // judul di set kosong
        cartesian.title("");

        // sklala minimum untuk y diber 0
        cartesian.yScale().minimum(0d);

        // kode warna hex untuk hijau
        String green = "#2FD99B";

        // tambahkan kode warna ke pallete
        cartesian.palette(new String[]{green});

        // kondisi warna kolom hijau
        column.normal().stroke(green);

        // isi kolom hjau
        column.hovered().fill(green + " 0.1");

        // kondisi warna kolom hijau
        column.hovered().stroke(green + " 2");

        // isi kolom hjau dengan transparansi setengah
        column.selected().fill(green, 0.5);

        // kondisi warna kolom hijau
        column.selected().stroke(green + " 4");

        // atur label untuk aksis y dengan format
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        // atur label untuk aksis y dengan ukuran font 8
        cartesian.xAxis(0).labels().fontSize(8);

        // atur tooltip mode posisi ke point
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        // atur label untuk aksis x interaktif mode jika x dihover
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        // judul untuk aksis x kosong
        cartesian.xAxis(0).title("");

        // judul untuk aksis y kosong
        cartesian.yAxis(0).title("");

        // tampilkan chart diagram batang
        chart.setChart(cartesian);
    }

    // fungsi untuk menampilkan deskrips
    private void showDescription(){

        // memberitahu adapter jika data berubah
        adapterCityModel.notifyDataSetChanged();
    }

    // ------------- //


    // fungsi yg akan dipanggil jika data response didapat
    @Override
    public void onGetAllCity(@Nullable ArrayList<CityModel> all) {

        // jika tidak nosong
        if (all != null){

            // tambahkan semua data ke array
            cityModels.addAll(all);
        }

        // tampilkan statistik
        showStatistic();

        // tampilkan deskripsi
        showDescription();
    }

    // fungsi yg dipanggil dan akan menampilkan progress
    @Override
    public void showProgressAllCity(Boolean show) {

        // tampilkan layot loading
        loadingLayout.setVisibility(show);
    }

    // fungsi yg akan menampilkan error
    @Override
    public void showErrorAllCity(String error) {
        if (BuildConfig.DEBUG){
            errorLayout.setMessage(error);
        }

        // tampilkan layout error
        errorLayout.show();
    }

    // ------------- //

    // fungsi yg akan dipanggil saat
    // activity dihancurkan
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // memanggil fungsi unsubscribe
        presenter.unsubscribe();
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
