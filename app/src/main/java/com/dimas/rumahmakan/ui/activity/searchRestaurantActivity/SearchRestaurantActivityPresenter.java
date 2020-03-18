package com.dimas.rumahmakan.ui.activity.searchRestaurantActivity;

import com.dimas.rumahmakan.model.responseModel.ResponseModel;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.service.RetrofitService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchRestaurantActivityPresenter implements SearchRestaurantActivityContract.Presenter {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private RetrofitService api  = RetrofitService.create();
    private SearchRestaurantActivityContract.View view;

    @Override
    public void getAllRestaurant(String searchBy, String searchValue, String orderBy, String orderDir, int offset, int limit, boolean enableLoading) {
        if (enableLoading) {
            view.showProgressAllRestaurant(true);
        }
        Disposable subscribe = api.allRestaurant(searchBy,searchValue,orderBy,orderDir,offset,limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseModel<ArrayList<RestaurantModel>>>() {
                    @Override
                    public void accept(ResponseModel<ArrayList<RestaurantModel>> result) throws Exception {
                        if (enableLoading) {
                            view.showProgressAllRestaurant(false);
                        }
                        if (result != null){
                            if (result.Error != null && !result.Error.isEmpty()){
                                view.showErrorAllRestaurant(result.Error);
                            }
                            view.onGetAllRestaurant(result.Data);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (enableLoading) {
                            view.showProgressAllRestaurant(false);
                        }
                        view.showErrorAllRestaurant(throwable.getMessage());
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
    public void attach(SearchRestaurantActivityContract.View view) {
        this.view = view;
    }

}
