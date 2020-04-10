package com.dimas.rumahmakan.ui.activity.routingActivity;

import com.dimas.rumahmakan.base.BaseContract;
import com.dimas.rumahmakan.service.RetrofitService;

import io.reactivex.disposables.CompositeDisposable;

// adalah class presenter untuk activity ini
// yg mana class ini akan menghandle
// fungsi-fungsi yg berkaitan dengan proses bisnis aplikasi
// seperti query ke api
public class RoutingActivityPresenter implements RoutingActivityContract.Presenter {

    // inisialisasi komposit disposal
    private CompositeDisposable subscriptions = new CompositeDisposable();

    // inisialisasi pool koneksi
    // ke api backend dengan retrofit
    private RetrofitService api  = RetrofitService.create();

    // deklarasi view
    private RoutingActivityContract.View view;

    // untuk saat ini fungsi ini belum dipakai
    @Override
    public void subscribe() { }

    // bersihkan seleuruh subscribsi
    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    // fungsi inisialisasi view
    @Override
    public void attach(RoutingActivityContract.View view) {
        this.view = view;
    }
}
