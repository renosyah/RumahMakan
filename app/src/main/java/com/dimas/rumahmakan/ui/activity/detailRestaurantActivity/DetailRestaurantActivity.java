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
import com.dimas.rumahmakan.util.Unit;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_DETAIL_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_EXPLORE_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SEARCH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.BACK_TO_SPLASH_ACTIVITY;
import static com.dimas.rumahmakan.util.StaticVariabel.NO_NEED_TO_BACK;

public class DetailRestaurantActivity extends AppCompatActivity implements  DetailRestaurantActivityContract.View {

    @Inject
    public DetailRestaurantActivityContract.Presenter presenter;

    private Context context;
    private Intent intent;
    private int backTo;

    private ImageView imageRestaurant;
    private TextView restaurantName;
    private TextView restaurantAddress;
    private TextView restaurantDetail;

    private ImageView backButton;
    private LinearLayout showRoute;
    private LinearLayout showMenu;

    private RestaurantModel restaurantModel;

    private View loadingDetailData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        initWidget();
    }

    private void initWidget(){
        context = this;
        intent = getIntent();

        restaurantModel = (RestaurantModel) intent.getSerializableExtra("data");
        backTo = intent.getIntExtra("back_to", BACK_TO_EXPLORE_ACTIVITY);

        injectDependency();
        presenter.attach(this);
        presenter.subscribe();

        loadingDetailData = findViewById(R.id.loading_detail_data_layout);
        loadingDetailData.setVisibility(View.VISIBLE);

        imageRestaurant = findViewById(R.id.restaurant_image);

        restaurantName = findViewById(R.id.restaurant_name);
        restaurantName.setText("");

        restaurantAddress = findViewById(R.id.restaurant_address);
        restaurantAddress.setText("");

        restaurantDetail = findViewById(R.id.detail_text);
        restaurantDetail.setText("");

        backButton = findViewById(R.id.back_image);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        showRoute = findViewById(R.id.show_route);
        showRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RoutingActivity.class);
                i.putExtra("data",restaurantModel);
                i.putExtra("back_to",NO_NEED_TO_BACK);
                startActivity(i);
            }
        });
        showMenu = findViewById(R.id.show_menu);
        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantModel.UrlMenu));
                startActivity(browserIntent);
            }
        });

        getOneRestaurant();
    }


    private void getOneRestaurant(){
        presenter.getOneRestaurant(restaurantModel.Id,true);
    }

    @Override
    public void onGetOneRestaurant(@Nullable RestaurantModel r) {
        if (r != null){
            Picasso.get()
                    .load(r.UrlImage)
                    .into(imageRestaurant);
            restaurantName.setText(r.Name);
            restaurantAddress.setText(r.Address);
            restaurantDetail.setText(r.Description);

            restaurantModel = r;
        }
    }

    // ------------ //

    @Override
    public void showProgressOneRestaurant(Boolean show) {
        loadingDetailData.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorOneRestaurant(String error) {
        new DialogNoInternet(context, new Unit<Boolean>() {
            @Override
            public void invoke(Boolean o) {
                startActivity(new Intent(context, ExploreActivity.class));
                finish();
            }
        }).show();
    }

    // ------------ //

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
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
