package com.dimas.rumahmakan.di.module;

import android.app.Application;

import com.dimas.rumahmakan.BaseApp;
import com.dimas.rumahmakan.di.scope.PerApplication;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private BaseApp baseApp;

    public ApplicationModule(BaseApp baseApp){
        this.baseApp = baseApp;
    }

    @Provides
    @Singleton
    @PerApplication
    public Application provideApplication() {
        return baseApp;
    }
}