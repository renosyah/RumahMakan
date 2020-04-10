package com.dimas.rumahmakan.ui.activity.searchRestaurantActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivityContract;

import java.util.ArrayList;

import retrofit2.http.Query;

// adalah class contract untuk activity ini
// yg mana class ini akan menghandle
// fungsi-fungsi apa saja yg dibutkan untuk
// komunikasi antar view dengan presenter
public class SearchRestaurantActivityContract {

    // inteface view yg akan diimplement oleh
    // view seperti aktivity atau fragment
    public interface View extends BaseContract.View {

        // fungsi response jika data didapat
        public void onGetAllRestaurant(@Nullable ArrayList<RestaurantModel> restaurantModels);

        // fungsi loading
        public void showProgressAllRestaurant(Boolean show);

        // fungsi response error
        public void showErrorAllRestaurant(String error);

    }

    // inteface presenter yg akan diimplement oleh
    // presenter seperti aktivity presenter atau fragment presenter
    public interface Presenter extends BaseContract.Presenter<View> {

        // fungsi yg akan digunakan sebagai request
        public void getAllRestaurant(
                String searchBy,
                String searchValue,
                String orderBy,
                String orderDir,
                int offset,
                int limit,
                boolean enableLoading
        );
    }
}
