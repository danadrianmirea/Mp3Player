package com.example.mike.mp3player.dagger.modules.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;

import com.example.mike.mp3player.service.player.MarshmallowMediaPlayerAdapterBase;
import com.example.mike.mp3player.service.player.MediaPlayerAdapterBase;
import com.example.mike.mp3player.service.player.NougatMediaPlayerAdapterBase;
import com.example.mike.mp3player.service.player.OreoPlayerAdapterBase;
import com.example.mike.mp3player.service.session.MediaSessionCallback;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MediaPlayerAdapterModule {

    @Singleton
    @Provides
    public MediaPlayerAdapterBase provideMediaPlayerAdapter(Context context, MediaPlayer.OnCompletionListener onCompletionListener,
                                                            MediaPlayer.OnSeekCompleteListener seekCompleteListener) {
        return createMediaPlayerAdapter(context, onCompletionListener, seekCompleteListener);
    }

    @Provides
    public MediaPlayer.OnSeekCompleteListener provideOnSeekCompleteListener(
            MediaSessionCallback mediaSessionCallback) {
        return mediaSessionCallback;
    }

    @Provides
    public MediaPlayer.OnCompletionListener provideOnCompletionListener(
            MediaSessionCallback mediaSessionCallback) {
        return mediaSessionCallback;
    }


    /**
     * @return the appropriate Media Player object
     */
    private MediaPlayerAdapterBase createMediaPlayerAdapter(Context context, MediaPlayer.OnCompletionListener onCompletionListener,
                                                            MediaPlayer.OnSeekCompleteListener seekCompleteListener) {
        switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.M:
                return new MarshmallowMediaPlayerAdapterBase(context, onCompletionListener, seekCompleteListener);
            case Build.VERSION_CODES.N:
                return new NougatMediaPlayerAdapterBase(context, onCompletionListener, seekCompleteListener);
            default: return new OreoPlayerAdapterBase(context, onCompletionListener, seekCompleteListener);
        }
    }
}
