package com.dimas.rumahmakan;

import android.app.Application;

import com.dimas.rumahmakan.di.component.ApplicationComponent;
import com.dimas.rumahmakan.di.component.DaggerApplicationComponent;
import com.dimas.rumahmakan.di.module.ApplicationModule;
import com.squareup.picasso.BuildConfig;

public class BaseApp extends Application {

    public static BaseApp instance = new BaseApp();
    private ApplicationComponent component;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        setup();

        if (BuildConfig.DEBUG){

        }
    }

    private void setup(){
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        component.inject(this);
    }
}
