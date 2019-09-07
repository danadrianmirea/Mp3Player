package com.example.mike.mp3player.dagger.modules;

import com.example.mike.mp3player.client.MediaControllerAdapter;
import com.example.mike.mp3player.client.SeekerBarController2;
import com.example.mike.mp3player.client.views.TimeCounter;
import com.example.mike.mp3player.dagger.scopes.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class SeekerBarModule {

    @FragmentScope
    @Provides
    public SeekerBarController2 provideSeekerBarController(MediaControllerAdapter mediaControllerAdapter,
                                                   TimeCounter timeCounter) {
        return new SeekerBarController2(mediaControllerAdapter, timeCounter);
    }
}
