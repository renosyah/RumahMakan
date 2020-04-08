package com.dimas.rumahmakan.di.module;

import android.app.Activity;

import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivityContract;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivityPresenter;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivityContract;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivityPresenter;
import com.dimas.rumahmakan.ui.activity.routingActivity.RoutingActivityContract;
import com.dimas.rumahmakan.ui.activity.routingActivity.RoutingActivityPresenter;
import com.dimas.rumahmakan.ui.activity.searchRestaurantActivity.SearchRestaurantActivityContract;
import com.dimas.rumahmakan.ui.activity.searchRestaurantActivity.SearchRestaurantActivityPresenter;
import com.dimas.rumahmakan.ui.activity.statistikActivity.StatistikActivityContract;
import com.dimas.rumahmakan.ui.activity.statistikActivity.StatistikActivityPresenter;

import dagger.Module;
import dagger.Provides;


// ini adalah class dimana
// setiap melakukan injecksi
// presenter ke activity
// maka akan di provide presenter
// untuk aktivity yg bersangkutan
@Module
public class ActivityModule {

    // dekalrasi variabel activity
    private Activity activity;

    // konstruktor class
    public ActivityModule(Activity activity){
        this.activity = activity;
    }

    // fungsi untuk provide activity
    // dengan nilai balik adalah variabel activity
    // yg telah diinisialisasi
    @Provides
    public Activity provideActivity()  {
        return activity;
    }


    // fungsi untuk provide activity
    // pada aktivity ExploreActivity
    @Provides
    public ExploreActivityContract.Presenter provideExploreActivityPresenter() {
        return new ExploreActivityPresenter();
    }

    // fungsi untuk provide activity
    // pada aktivity DetailRestaurantActivity
    @Provides
    public DetailRestaurantActivityContract.Presenter provideDetailRestaurantActivityPresenter() {
        return new DetailRestaurantActivityPresenter();
    }

    // fungsi untuk provide activity
    // pada aktivity SearchRestaurantActivity
    @Provides
    public SearchRestaurantActivityContract.Presenter provideSearchRestaurantActivityPresenter() {
        return new SearchRestaurantActivityPresenter();
    }

    // fungsi untuk provide activity
    // pada aktivity RoutingActivity
    @Provides
    public RoutingActivityContract.Presenter provideRoutingActivityPresenter() {
        return new RoutingActivityPresenter();
    }

    // fungsi untuk provide activity
    // pada aktivity StatistikActivity
    @Provides
    public StatistikActivityContract.Presenter provideStatistikActivityPresenter() {
        return new StatistikActivityPresenter();
    }
}
