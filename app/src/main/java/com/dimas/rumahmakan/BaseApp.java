package com.dimas.rumahmakan;

import android.app.Application;

import com.dimas.rumahmakan.di.component.ApplicationComponent;
import com.dimas.rumahmakan.di.component.DaggerApplicationComponent;
import com.dimas.rumahmakan.di.module.ApplicationModule;
import com.squareup.picasso.BuildConfig;

// class ini adalah base app
// untuk aplikasi ini
// normalnya setiap project memiliki setidaknya
// satu app atau lebih
// base app ini akan membedakan aplikasi satu dengan yg lainnya
public class BaseApp extends Application {

    // instance base app
    // menggunakan konsep singleton
    public static BaseApp instance = new BaseApp();

    // deklarasi variabel componen
    private ApplicationComponent component;


    // fungsi akan dipanggil
    // pertama kali
    @Override
    public void onCreate() {
        super.onCreate();

        // inisiasi instance
        instance = this;

        // memanggil fungsi setup
        setup();

        // jika aplikasi di run dengan mode debug
        // lakukan sesuatu
        if (BuildConfig.DEBUG){

        }
    }

    // pemanggilan register
    // dependensi injeksi untuk base app ini
    private void setup(){

        // menginisialisasi variabel component
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        // memanggil fungsi inject
        component.inject(this);
    }
}
