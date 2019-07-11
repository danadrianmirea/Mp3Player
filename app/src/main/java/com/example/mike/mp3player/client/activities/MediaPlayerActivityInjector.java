package com.example.mike.mp3player.client.activities;

import android.os.Bundle;

import com.example.mike.mp3player.dagger.components.fragments.DaggerMediaActivityCompatComponent;
import com.example.mike.mp3player.dagger.components.fragments.MediaActivityCompatComponent;

/**
 * Media Player Activity injector
 */
public class MediaPlayerActivityInjector extends MediaPlayerActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        initialiseDependencies();
        super.onCreate(savedInstance);
    }
    @Override
    void initialiseDependencies() {
        MediaActivityCompatComponent mediaActivityCompatComponent = DaggerMediaActivityCompatComponent
                .factory()
                .create(getApplicationContext(), getWorkerId(), getSubscriptionType(), this);
                mediaActivityCompatComponent.inject(this);
        this.setMediaActivityCompatComponent(mediaActivityCompatComponent);
    }
}
