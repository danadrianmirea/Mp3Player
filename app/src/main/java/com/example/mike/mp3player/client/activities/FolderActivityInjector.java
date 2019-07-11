package com.example.mike.mp3player.client.activities;

import android.os.Bundle;

import com.example.mike.mp3player.dagger.components.fragments.DaggerMediaActivityCompatComponent;
import com.example.mike.mp3player.dagger.components.fragments.MediaActivityCompatComponent;

public class FolderActivityInjector extends FolderActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        initialiseDependencies();
        super.onCreate(savedInstance);
    }
    @Override
    void initialiseDependencies() {
        MediaActivityCompatComponent component = DaggerMediaActivityCompatComponent
                .factory()
                .create(getApplicationContext(), getWorkerId(), getSubscriptionType(), this);
        this.setMediaActivityCompatComponent(component);
        component.inject(this);

    }
}
