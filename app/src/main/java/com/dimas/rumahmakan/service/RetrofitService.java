package com.dimas.rumahmakan.service;

import com.dimas.rumahmakan.BuildConfig;
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
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RetrofitService {

    // add more end point to access
    @GET("api/all_rumah_makan.php")
    public Observable<ResponseModel<ArrayList<RestaurantModel>>> allRestaurant(
            @Query("search_by") String searchBy,
            @Query("search_value") String searchValue,
            @Query("order_by") String orderBy,
            @Query("order_dir") String orderDir,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

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
    @GET("api/one_rumah_makan.php")
    public Observable<ResponseModel<RestaurantModel>> oneRestaurant(
            @Query("id") int id
    );


    public static RetrofitService create()  {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

       Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
               .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BuildConfig.SERVER_URL)
                .build();

        return retrofit.create(RetrofitService.class);
    }
}
