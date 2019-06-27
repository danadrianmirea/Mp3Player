package com.example.mike.mp3player.dagger.modules.service;

import com.example.mike.mp3player.client.activities.SplashScreenEntryActivity;
import com.example.mike.mp3player.dagger.components.SplashScreenEntryActivitySubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = SplashScreenEntryActivitySubcomponent.class)
public abstract class SplashScreenEntryActivityModule {

    @Binds
    @IntoMap
    @ClassKey(SplashScreenEntryActivity.class)
    abstract AndroidInjector.Factory<?>  provideSplashScreenEntryActivity(
            SplashScreenEntryActivitySubcomponent.Factory factory);
}
