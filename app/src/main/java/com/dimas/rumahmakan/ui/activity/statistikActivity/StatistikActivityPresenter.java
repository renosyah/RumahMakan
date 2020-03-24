package com.dimas.rumahmakan.ui.activity.statistikActivity;

import com.dimas.rumahmakan.model.cityModel.CityModel;
import com.dimas.rumahmakan.model.responseModel.ResponseModel;
import com.dimas.rumahmakan.service.RetrofitService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class StatistikActivityPresenter implements StatistikActivityContract.Presenter {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private RetrofitService api  = RetrofitService.create();
    private StatistikActivityContract.View view;

    @Override
    public void getAllCity(String searchBy, String searchValue, String orderBy, String orderDir, int offset, int limit, boolean enableLoading) {
        if (enableLoading) {
            view.showProgressAllCity(true);
        }
        Disposable subscribe = api.allCity(searchBy,searchValue,orderBy,orderDir,offset,limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseModel<ArrayList<CityModel>>>() {
                    @Override
                    public void accept(ResponseModel<ArrayList<CityModel>> result) throws Exception {
                        if (enableLoading) {
                            view.showProgressAllCity(false);
                        }
                        if (result != null){
                            if (result.Error != null && !result.Error.isEmpty()){
                                view.showErrorAllCity(result.Error);
                            }
                            view.onGetAllCity(result.Data);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (enableLoading) {
                            view.showProgressAllCity(false);
                        }
                        view.showErrorAllCity(throwable.getMessage());
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
    public void attach(StatistikActivityContract.View view) {
        this.view = view;
    }


}
