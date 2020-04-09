package com.dimas.rumahmakan.ui.activity.detailRestaurantActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;

// adalah class contract untuk activity ini
// yg mana class ini akan menghandle
// fungsi-fungsi apa saja yg dibutkan untuk
// komunikasi antar view dengan presenter
public class DetailRestaurantActivityContract {

    // inteface view yg akan diimplement oleh
    // view seperti aktivity atau fragment
    public interface View extends BaseContract.View {

        // fungsi response saat mendapatkan data
        public void onGetOneRestaurant(@Nullable RestaurantModel restaurantModel);

        // fungsi response saat progress atau loading
        public void showProgressOneRestaurant(Boolean show);

        // fungsi response saat mendapatkan error
        public void showErrorOneRestaurant(String error);

    }

    // inteface presenter yg akan diimplement oleh
    // presenter seperti aktivity presenter atau fragment presenter
    public interface Presenter extends BaseContract.Presenter<View> {

        // fungsi untyk mendapatkan 1 data
        // info detail wisata kuliner
        public void getOneRestaurant(int id,boolean enableLoading);
    }
}