package com.dimas.rumahmakan.ui.activity.detailRestaurantActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;

public class DetailRestaurantActivityContract {

    public interface View extends BaseContract.View {
        // add more for response
        public void onGetOneRestaurant(@Nullable RestaurantModel restaurantModel);
        public void showProgressOneRestaurant(Boolean show);
        public void showErrorOneRestaurant(String error);

    }
    public interface Presenter extends BaseContract.Presenter<View> {
        // add for request
        public void getOneRestaurant(int id,boolean enableLoading);
    }
}