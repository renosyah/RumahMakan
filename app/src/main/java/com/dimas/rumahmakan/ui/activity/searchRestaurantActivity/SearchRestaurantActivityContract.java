package com.dimas.rumahmakan.ui.activity.searchRestaurantActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.ui.activity.detailRestaurantActivity.DetailRestaurantActivityContract;

import java.util.ArrayList;

public class SearchRestaurantActivityContract {
    public interface View extends BaseContract.View {
        // add more for response
        public void onGetAllNearestRestaurant(@Nullable ArrayList<RestaurantModel> restaurantModels);
        public void showProgressAllNearestRestaurant(Boolean show);
        public void showErrorAllNearestRestaurant(String error);

    }
    public interface Presenter extends BaseContract.Presenter<View> {
        // add for request
        public void getAllNearestRestaurant(double curLatitude,
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
