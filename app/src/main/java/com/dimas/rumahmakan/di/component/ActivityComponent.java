package com.dimas.rumahmakan.di.component;

import com.dimas.rumahmakan.di.module.ActivityModule;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivity;
import com.dimas.rumahmakan.ui.activity.routingActivity.RoutingActivity;
import com.dimas.rumahmakan.ui.activity.searchRestaurantActivity.SearchRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.statistikActivity.StatistikActivity;

import dagger.Component;

@Component(modules = { ActivityModule.class })
public interface ActivityComponent {

    // add for each new activity
    void inject(ExploreActivity exploreActivity);
    void inject(DetailRestaurantActivity detailRestaurantActivity);
    void inject(SearchRestaurantActivity searchRestaurantActivity);
    void inject(RoutingActivity routingActivity);
    void inject(StatistikActivity statistikActivity);
}
