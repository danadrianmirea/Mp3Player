package com.example.mike.mp3player.client;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.media.session.MediaControllerCompat;

import java.util.List;

public class MediaControllerWrapper< A extends AppCompatActivity>  {

    private MediaControllerCompat mediaControllerCompat;
    private A activity;
    private MediaSessionCompat.Token token;
    private boolean isInitialized = false;

    public MediaControllerWrapper(A activity, MediaSessionCompat.Token token) {
        this.activity = activity;
        this.token = token;
    }

    public boolean init() {
        try {
            this.mediaControllerCompat = new MediaControllerCompat(activity.getApplicationContext(), token);
        } catch (RemoteException ex) {
            this.isInitialized = false;
            return false;
        }

        this.isInitialized = true;
        return true;
    }

    public boolean registerCallback(MediaControllerCompat.Callback callback) {
        getMediaControllerCompat().registerCallback(callback);
        return true;
    }

    public void prepareFromMediaId(String mediaId, Bundle extras) {
        getMediaControllerCompat().getTransportControls().prepareFromMediaId(mediaId, extras);
    }

    public void play() {
        getMediaControllerCompat().getTransportControls().play();
    }

    public void pause() {
        getMediaControllerCompat().getTransportControls().pause();
    }

    public void stop() {
        getMediaControllerCompat().getTransportControls().stop();
    }

    public void skipToNext() {
        getMediaControllerCompat().getTransportControls().skipToNext();
    }

    public void skipToPrevious() {
        getMediaControllerCompat().getTransportControls().skipToPrevious();
    }

    public int getPlaybackState() {
        if (getMediaControllerCompat() != null && getMediaControllerCompat().getPlaybackState() != null) {
            return getMediaControllerCompat().getPlaybackState().getState();
        }
        return 0;
    }

    public PlaybackStateCompat getPlaybackStateAsCompat() {
        if (getMediaControllerCompat() != null ) {
            return getMediaControllerCompat().getPlaybackState();
        }
        return null;
    }

    public MediaControllerCompat getMediaControllerCompat() {
        return mediaControllerCompat;
    }

    public MediaMetadataCompat getMetaData() {
        return mediaControllerCompat.getMetadata();
    }

    public void disconnect(List<MediaControllerCompat.Callback> callbacks) {
        if (getMediaControllerCompat() != null) {
            // find a way to disconnect all callbacks
            for (MediaControllerCompat.Callback callback : callbacks) {
                getMediaControllerCompat().unregisterCallback(callback);
            }
        }
    }

}
