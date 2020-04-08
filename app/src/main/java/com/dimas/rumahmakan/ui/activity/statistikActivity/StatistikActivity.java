package com.dimas.rumahmakan.ui.activity.statistikActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.data.Set;
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

import java.util.ArrayList;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.dimas.rumahmakan.util.Unit;

import javax.inject.Inject;

import static com.dimas.rumahmakan.util.CheckService.isInternetConnected;

public class StatistikActivity extends AppCompatActivity implements StatistikActivityContract.View {

    @Inject
    public StatistikActivityContract.Presenter presenter;

    private Context context;
    private AnyChartView chart;
    private Button back;

    private RecyclerView listDescription;
    private AdapterCityModel adapterCityModel;
    private ArrayList<CityModel> cityModels = new ArrayList<>();
    private int offset = 0,limit = 10;
    private String searchBy = "nama",searchValue = "";
    private String orderBy = "nama",orderDir = "asc";

    private LoadingLayout loadingLayout;
    private ErrorLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);
        initWidget();
    }

    private void initWidget(){
        context = this;

        injectDependency();
        presenter.attach(this);
        presenter.subscribe();

        chart = findViewById(R.id.chart);
        chart.setEnabled(false);
        chart.setOnRenderedListener(new AnyChartView.OnRenderedListener() {
            @Override
            public void onRendered() {
                loadingLayout.setVisibility(false);
            }
        });

        listDescription = findViewById(R.id.list_description);
        adapterCityModel = new AdapterCityModel(context,cityModels);
        listDescription.setAdapter(adapterCityModel);
        listDescription.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));

        loadingLayout = new LoadingLayout(context,findViewById(R.id.loading_layout));
        loadingLayout.setMessage(context.getString(R.string.init_chart));

        errorLayout = new ErrorLayout(context, findViewById(R.id.error_layout), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getIntent());
                finish();
            }
        });
        errorLayout.setMessage(context.getString(R.string.error_common));


        back = findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!isInternetConnected(context)) {
            new DialogNoInternet(context, new Unit<Boolean>() {
                @Override
                public void invoke(Boolean o) {

                }
            }).show();
        }

        getAllCity();
    }

    private void getAllCity(){
        presenter.getAllCity(searchBy,searchValue,orderBy,orderDir,offset,limit,true);
    }

    private void showStatistic(){

        loadingLayout.setVisibility(true);

        Cartesian cartesian = AnyChart.column();

        ArrayList<DataEntry> data = new ArrayList<DataEntry>();
        for (CityModel c : cityModels){
            data.add(new ValueDataEntry(c.Name,c.Total));
        }

        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("");
        cartesian.yScale().minimum(0d);

        String green = "#2FD99B";
        cartesian.palette(new String[]{green});
        column.normal().stroke(green);
        column.hovered().fill(green + " 0.1");
        column.hovered().stroke(green + " 2");
        column.selected().fill(green, 0.5);
        column.selected().stroke(green + " 4");

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        cartesian.xAxis(0).labels().fontSize(8);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("");
        cartesian.yAxis(0).title("");

        chart.setChart(cartesian);

    }

    private void showDescription(){
        adapterCityModel.notifyDataSetChanged();
    }

    // ------------- //

    @Override
    public void onGetAllCity(@Nullable ArrayList<CityModel> all) {
        if (all != null){
            cityModels.addAll(all);
        }
        showStatistic();
        showDescription();
    }

    @Override
    public void showProgressAllCity(Boolean show) {
        loadingLayout.setVisibility(show);
    }

    @Override
    public void showErrorAllCity(String error) {
        if (BuildConfig.DEBUG){
            errorLayout.setMessage(error);
        }
        errorLayout.show();
    }

    // ------------- //

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
    }

    private void injectDependency(){
        ActivityComponent listcomponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();

        listcomponent.inject(this);
    }
}
