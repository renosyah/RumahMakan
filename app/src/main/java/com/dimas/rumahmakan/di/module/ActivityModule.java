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

@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity){
        this.activity = activity;
    }

    @Provides
    public Activity provideActivity()  {
        return activity;
    }


    // add more
    @Provides
    public ExploreActivityContract.Presenter provideExploreActivityPresenter() {
        return new ExploreActivityPresenter();
    }

    @Provides
    public DetailRestaurantActivityContract.Presenter provideDetailRestaurantActivityPresenter() {
        return new DetailRestaurantActivityPresenter();
    }

    @Provides
    public SearchRestaurantActivityContract.Presenter provideSearchRestaurantActivityPresenter() {
        return new SearchRestaurantActivityPresenter();
    }

    @Provides
    public RoutingActivityContract.Presenter provideRoutingActivityPresenter() {
        return new RoutingActivityPresenter();
    }

    @Provides
    public StatistikActivityContract.Presenter provideStatistikActivityPresenter() {
        return new StatistikActivityPresenter();
    }
}
