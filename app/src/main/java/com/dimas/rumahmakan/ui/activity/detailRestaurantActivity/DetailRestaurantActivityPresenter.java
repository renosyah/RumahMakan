package com.dimas.rumahmakan.ui.activity.detailRestaurantActivity;

import com.dimas.rumahmakan.model.responseModel.ResponseModel;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.service.RetrofitService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DetailRestaurantActivityPresenter implements DetailRestaurantActivityContract.Presenter {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private RetrofitService api  = RetrofitService.create();
    private DetailRestaurantActivityContract.View view;

    @Override
    public void getOneRestaurant(int id, boolean enableLoading) {
        if (enableLoading) {
            view.showProgressOneRestaurant(true);
        }
        Disposable subscribe = api.oneRestaurant(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseModel<RestaurantModel>>() {
                    @Override
                    public void accept(ResponseModel<RestaurantModel> result) throws Exception {
                        if (enableLoading) {
                            view.showProgressOneRestaurant(false);
                        }
                        if (result != null){
                            if (result.Error != null && !result.Error.isEmpty()){
                                view.showErrorOneRestaurant(result.Error);
                            }
                            view.onGetOneRestaurant(result.Data);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (enableLoading) {
                            view.showProgressOneRestaurant(false);
                        }
                        view.showErrorOneRestaurant(throwable.getMessage());
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
    public void attach(DetailRestaurantActivityContract.View view) {
        this.view = view;
    }


}
