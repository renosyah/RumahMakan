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

// adalah class presenter untuk activity ini
// yg mana class ini akan menghandle
// fungsi-fungsi yg berkaitan dengan proses bisnis aplikasi
// seperti query ke api
public class SearchRestaurantActivityPresenter implements SearchRestaurantActivityContract.Presenter {

    // inisialisasi komposit disposal
    private CompositeDisposable subscriptions = new CompositeDisposable();

    // inisialisasi pool koneksi
    // ke api backend dengan retrofit
    private RetrofitService api  = RetrofitService.create();

    // deklarasi view
    private SearchRestaurantActivityContract.View view;

    @Override
    public void getAllRestaurant(String searchBy, String searchValue, String orderBy, String orderDir, int offset, int limit, boolean enableLoading) {

        // apakah loading digunakan
        if (enableLoading) {

            // tampilkan loading
            view.showProgressAllRestaurant(true);
        }

        // membuat instance thread untuk request
        Disposable subscribe = api.allRestaurant(searchBy,searchValue,orderBy,orderDir,offset,limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseModel<ArrayList<RestaurantModel>>>() {

                    // pada saat berhasil request
                    // dan mendapatkan response
                    @Override
                    public void accept(ResponseModel<ArrayList<RestaurantModel>> result) throws Exception {

                        // apakah loading digunakan
                        if (enableLoading) {

                            // jangan tampilkan loading
                            view.showProgressAllRestaurant(false);
                        }

                        // jika hasil bukan null
                        if (result != null){

                            // dan error bukan null
                            // dan error kosong
                            if (result.Error != null && !result.Error.isEmpty()){

                                // tampilkan error
                                view.showErrorAllRestaurant(result.Error);
                            }

                            // tampilkan data
                            view.onGetAllRestaurant(result.Data);
                        }
                    }
                }, new Consumer<Throwable>() {

                    // pada saat gagal request
                    // dan mendapatkan response error
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // apakah loading digunakan
                        if (enableLoading) {

                            // jangan tampilkan loading
                            view.showProgressAllRestaurant(false);
                        }

                        // tampilkan error
                        view.showErrorAllRestaurant(throwable.getMessage());
                    }
                });

        // tambahkan ke subscription
        subscriptions.add(subscribe);
    }

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
    public void attach(SearchRestaurantActivityContract.View view) {
        this.view = view;
    }

}
