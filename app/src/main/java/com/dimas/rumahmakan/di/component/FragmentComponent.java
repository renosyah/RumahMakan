package com.dimas.rumahmakan.di.component;

import com.dimas.rumahmakan.di.module.FragmentModule;

import dagger.Component;

@Component(modules = { FragmentModule.class })
public interface FragmentComponent {
    // add for each new fragment
}
