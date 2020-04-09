package com.dimas.rumahmakan.ui.activity.detailRestaurantActivity;

import com.dimas.rumahmakan.model.responseModel.ResponseModel;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.dimas.rumahmakan.service.RetrofitService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

// adalah class presenter untuk activity ini
// yg mana class ini akan menghandle
// fungsi-fungsi yg berkaitan dengan proses bisnis aplikasi
// seperti query ke api
public class DetailRestaurantActivityPresenter implements DetailRestaurantActivityContract.Presenter {

    // inisialisasi komposit disposal
    private CompositeDisposable subscriptions = new CompositeDisposable();

    // inisialisasi pool koneksi
    // ke api backend dengan retrofit
    private RetrofitService api  = RetrofitService.create();

    // deklarasi view
    private DetailRestaurantActivityContract.View view;

    // fungsi yg akan dipanggil
    // saat ingin request data
    @Override
    public void getOneRestaurant(int id, boolean enableLoading) {

        // apakah loading digunakan
        if (enableLoading) {

            // tampilkan loading
            view.showProgressOneRestaurant(true);
        }

        // membuat instance thread untuk request
        Disposable subscribe = api.oneRestaurant(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseModel<RestaurantModel>>() {

                    // pada saat berhasil request
                    // dan mendapatkan response
                    @Override
                    public void accept(ResponseModel<RestaurantModel> result) throws Exception {

                        // apakah loading digunakan
                        if (enableLoading) {

                            // jangan tampilkan loading
                            view.showProgressOneRestaurant(false);
                        }

                        // jika hasil bukan null
                        if (result != null){

                            // dan error bukan null
                            // dan error kosong
                            if (result.Error != null && !result.Error.isEmpty()){

                                // tampilkan error
                                view.showErrorOneRestaurant(result.Error);
                            }

                            // tampilkan data
                            view.onGetOneRestaurant(result.Data);
                        }
                    }

                    // pada saat gagal request
                    // dan mendapatkan response error
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // apakah loading digunakan
                        if (enableLoading) {

                            // jangan tampilkan loading
                            view.showProgressOneRestaurant(false);
                        }

                        // tampilkan error
                        view.showErrorOneRestaurant(throwable.getMessage());
                    }
                });

        // tambahkan ke subscription
        subscriptions.add(subscribe);
    }


    // untuk saat ini fungsi ini belum dipakai
    @Override
    public void subscribe() {

    }

    // bersihkan seleuruh subscribsi
    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }


    // fungsi inisialisasi view
    @Override
    public void attach(DetailRestaurantActivityContract.View view) {
        this.view = view;
    }
}
