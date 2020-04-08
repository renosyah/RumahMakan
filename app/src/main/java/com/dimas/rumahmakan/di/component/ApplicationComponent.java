package com.dimas.rumahmakan.di.component;

import com.dimas.rumahmakan.BaseApp;
import com.dimas.rumahmakan.di.module.ApplicationModule;

import dagger.Component;

// ini adalah interface komponen base
// agar fungsi inject dapat dipanggil
// maka fungsi tersebut sebelumnya harus didelarasi
// di interface ini
@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {

    // fungsi yg akan digunakan untuk diinject di base
    void inject(BaseApp application);
}