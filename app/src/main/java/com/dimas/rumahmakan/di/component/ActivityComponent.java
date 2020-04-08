package com.dimas.rumahmakan.di.component;

import com.dimas.rumahmakan.di.module.ActivityModule;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.exploreActivity.ExploreActivity;
import com.dimas.rumahmakan.ui.activity.routingActivity.RoutingActivity;
import com.dimas.rumahmakan.ui.activity.searchRestaurantActivity.SearchRestaurantActivity;
import com.dimas.rumahmakan.ui.activity.statistikActivity.StatistikActivity;

import dagger.Component;


// ini adalah interface komponen aktivity
// agar fungsi inject dapat dipanggil
// maka fungsi tersebut sebelumnya harus didelarasi
// di interface ini
@Component(modules = { ActivityModule.class })
public interface ActivityComponent {

    // fungsi yg akan digunakan untuk diinject di activity ExploreActivity
    void inject(ExploreActivity exploreActivity);

    // fungsi yg akan digunakan untuk diinject di activity DetailRestaurantActivity
    void inject(DetailRestaurantActivity detailRestaurantActivity);

    // fungsi yg akan digunakan untuk diinject di activity SearchRestaurantActivity
    void inject(SearchRestaurantActivity searchRestaurantActivity);

    // fungsi yg akan digunakan untuk diinject di activity RoutingActivity
    void inject(RoutingActivity routingActivity);

    // fungsi yg akan digunakan untuk diinject di activity StatistikActivity
    void inject(StatistikActivity statistikActivity);
}
