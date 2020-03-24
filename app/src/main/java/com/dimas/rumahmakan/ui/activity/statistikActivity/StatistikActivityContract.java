package com.dimas.rumahmakan.ui.activity.statistikActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.cityModel.CityModel;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;

import java.util.ArrayList;

public class StatistikActivityContract {
    public interface View extends BaseContract.View {
        // add more for response
        public void onGetAllCity(@Nullable ArrayList<CityModel> cityModels);
        public void showProgressAllCity(Boolean show);
        public void showErrorAllCity(String error);
    }

    public interface Presenter extends BaseContract.Presenter<View> {
        // add for request
        public void getAllCity(String searchBy,
                                     String searchValue,
                                     String orderBy,
                                     String orderDir,
                                     int offset,
                                     int limit,
                                     boolean enableLoading
        );
    }
}
