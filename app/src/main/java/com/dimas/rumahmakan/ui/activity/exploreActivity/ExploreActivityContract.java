package com.dimas.rumahmakan.ui.activity.exploreActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

// adalah class contract untuk activity ini
// yg mana class ini akan menghandle
// fungsi-fungsi apa saja yg dibutkan untuk
// komunikasi antar view dengan presenter
public class ExploreActivityContract {

    // inteface view yg akan diimplement oleh
    // view seperti aktivity atau fragment
    public interface View extends BaseContract.View {

        // fungsi response saat mendapatkan data
        public void onGetAllNearestRestaurant(@Nullable ArrayList<RestaurantModel> restaurantModels);

        // fungsi response saat progress atau loading
        public void showProgressAllNearestRestaurant(Boolean show);

        // fungsi response saat mendapatkan error
        public void showErrorAllNearestRestaurant(String error);
    }

    // inteface presenter yg akan diimplement oleh
    // presenter seperti aktivity presenter atau fragment presenter
    public interface Presenter extends BaseContract.Presenter<View> {

        // fungsi untyk mendapatkan data
        // wisata kuliner terdekat
        public void getAllNearestRestaurant(
                double curLatitude,
                double curLongitude,
                double range,
                String searchBy,
                String searchValue,
                int offset,
                int limit,
                boolean enableLoading
        );
    }
}
