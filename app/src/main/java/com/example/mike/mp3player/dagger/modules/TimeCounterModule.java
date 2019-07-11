package com.example.mike.mp3player.dagger.modules;

import android.os.Handler;

import com.example.mike.mp3player.client.views.TimeCounter;
import com.example.mike.mp3player.dagger.scopes.AndroidComponentScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = MainHandlerModule.class)
public class TimeCounterModule {

    @AndroidComponentScope
    @Provides
    TimeCounter provideTimeCounter(@Named("main") Handler handler) {
        return new TimeCounter(handler);
    }
}
