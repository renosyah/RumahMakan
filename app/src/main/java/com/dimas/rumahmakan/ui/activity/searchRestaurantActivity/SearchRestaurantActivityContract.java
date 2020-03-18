package com.dimas.rumahmakan.ui.activity.searchRestaurantActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivityContract;

import java.util.ArrayList;

import retrofit2.http.Query;

public class SearchRestaurantActivityContract {
    public interface View extends BaseContract.View {
        // add more for response
        public void onGetAllRestaurant(@Nullable ArrayList<RestaurantModel> restaurantModels);
        public void showProgressAllRestaurant(Boolean show);
        public void showErrorAllRestaurant(String error);

    }
    public interface Presenter extends BaseContract.Presenter<View> {
        // add for request
        public void getAllRestaurant(String searchBy,
                                     String searchValue,
                                     String orderBy,
                                     String orderDir,
                                     int offset,
                                     int limit,
                                     boolean enableLoading
        );
    }
}
