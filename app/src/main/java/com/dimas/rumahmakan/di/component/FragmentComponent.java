package com.dimas.rumahmakan.di.component;

import com.dimas.rumahmakan.di.module.FragmentModule;

import dagger.Component;

// ini adalah interface komponen fragment
// agar fungsi inject dapat dipanggil
// maka fungsi tersebut sebelumnya harus didelarasi
// di interface ini
@Component(modules = { FragmentModule.class })
public interface FragmentComponent {}
