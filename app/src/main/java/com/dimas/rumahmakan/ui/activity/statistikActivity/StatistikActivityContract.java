package com.dimas.rumahmakan.ui.activity.statistikActivity;

import androidx.annotation.Nullable;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.model.cityModel.CityModel;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;

import java.util.ArrayList;

// adalah class contract untuk activity ini
// yg mana class ini akan menghandle
// fungsi-fungsi apa saja yg dibutkan untuk
// komunikasi antar view dengan presenter
public class StatistikActivityContract {

    // inteface view yg akan diimplement oleh
    // view seperti aktivity atau fragment
    public interface View extends BaseContract.View {

        // fungsi response jika data didapat
        public void onGetAllCity(@Nullable ArrayList<CityModel> cityModels);

        // fungsi loading
        public void showProgressAllCity(Boolean show);

        // fungsi response error
        public void showErrorAllCity(String error);
    }

    // inteface presenter yg akan diimplement oleh
    // presenter seperti aktivity presenter atau fragment presenter
    public interface Presenter extends BaseContract.Presenter<View> {

        // fungsi yg akan digunakan sebagai request
        public void getAllCity(
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
