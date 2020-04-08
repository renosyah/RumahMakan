package com.dimas.rumahmakan.service;

import com.dimas.rumahmakan.BuildConfig;
import com.dimas.rumahmakan.model.cityModel.CityModel;
import com.dimas.rumahmakan.model.responseModel.ResponseModel;
import com.dimas.rumahmakan.model.restaurantModel.RestaurantModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


// ini adalah interface service
// yg mana memiliki fungsi yg akan digunakan
// untuk melakukan request ke api
public interface RetrofitService {

    // deklarasi fungsi request ke daftar rumah makan
    // dengan parameter query
    @GET("api/all_rumah_makan.php")
    public Observable<ResponseModel<ArrayList<RestaurantModel>>> allRestaurant(
            @Query("search_by") String searchBy,
            @Query("search_value") String searchValue,
            @Query("order_by") String orderBy,
            @Query("order_dir") String orderDir,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    // deklarasi fungsi request ke daftar rumah makan terdekat
    // dengan parameter query
    // seperti lokasi user dan diameter
    // yg dipakai
    @GET("api/all_closes_rumah_makan.php")
    public Observable<ResponseModel<ArrayList<RestaurantModel>>> allNearestRestaurant(
            @Query("current_latitude") double curLatitude,
            @Query("current_longitude") double curLongitude,
            @Query("range") double range,
            @Query("search_by") String searchBy,
            @Query("search_value") String searchValue,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    // deklarasi fungsi request hanya satu data rumah makan
    // dengan parameter query
    @GET("api/one_rumah_makan.php")
    public Observable<ResponseModel<RestaurantModel>> oneRestaurant(
            @Query("id") int id
    );

    // deklarasi fungsi request ke daftar kota
    // dengan parameter query
    @GET("api/all_kota.php")
    public Observable<ResponseModel<ArrayList<CityModel>>> allCity(
            @Query("search_by") String searchBy,
            @Query("search_value") String searchValue,
            @Query("order_by") String orderBy,
            @Query("order_dir") String orderDir,
            @Query("offset") int offset,
            @Query("limit") int limit
    );


    // fungsi static yg nantinya akan
    // dipanggil, sama seperti koneksi ke db
    // namun ini untuk api
    public static RetrofitService create()  {

        // deklarasi gson builder
        // fungsinya agar dapat
        // melakukan parsing json
        // meskipun json kurang valid
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // membuat instance retrofit
        // yg nantinya ini yg akan digunakan untuk
        // melakukan request ke api
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BuildConfig.SERVER_URL)
                .build();

        // balikan instance
        // sebagai nilai balik
        return retrofit.create(RetrofitService.class);
    }
}
