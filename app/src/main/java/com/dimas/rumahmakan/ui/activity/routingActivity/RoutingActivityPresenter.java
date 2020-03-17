package com.dimas.rumahmakan.ui.activity.routingActivity;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.service.RetrofitService;

import io.reactivex.disposables.CompositeDisposable;

public class RoutingActivityPresenter implements RoutingActivityContract.Presenter {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private RetrofitService api  = RetrofitService.create();
    private RoutingActivityContract.View view;

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Override
    public void attach(RoutingActivityContract.View view) {
        this.view = view;
    }
}
