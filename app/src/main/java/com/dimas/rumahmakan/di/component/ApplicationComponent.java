package com.dimas.rumahmakan.di.component;

import com.dimas.rumahmakan.BaseApp;
import com.dimas.rumahmakan.di.module.ApplicationModule;

import dagger.Component;

@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {
    void inject(BaseApp application);
    // add for each new base
}