package com.dimas.rumahmakan.ui.activity.exploreActivity;

import com.dimas.rumahmakan.model.responseModel.ResponseModel;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.service.RetrofitService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExploreActivityPresenter implements ExploreActivityContract.Presenter {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private RetrofitService api  = RetrofitService.create();
    private  ExploreActivityContract.View view;

    @Override
    public void getAllNearestRestaurant(double curLatitude, double curLongitude, double range, String searchBy, String searchValue, int offset, int limit, boolean enableLoading) {
        if (enableLoading) {
            view.showProgressAllNearestRestaurant(true);
        }
        Disposable subscribe = api.allNearestRestaurant(curLatitude,curLongitude,range,searchBy,searchValue,offset,limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseModel<ArrayList<RestaurantModel>>>() {
                    @Override
                    public void accept(ResponseModel<ArrayList<RestaurantModel>> result) throws Exception {
                        if (enableLoading) {
                            view.showProgressAllNearestRestaurant(false);
                        }
                        if (result != null){
                            if (result.Error != null && !result.Error.isEmpty()){
                                view.showErrorAllNearestRestaurant(result.Error);
                            }
                            view.onGetAllNearestRestaurant(result.Data);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (enableLoading) {
                            view.showProgressAllNearestRestaurant(false);
                        }
                        view.showErrorAllNearestRestaurant(throwable.getMessage());
                    }
                });

        subscriptions.add(subscribe);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Override
    public void attach(ExploreActivityContract.View view) {
        this.view = view;
    }



}
