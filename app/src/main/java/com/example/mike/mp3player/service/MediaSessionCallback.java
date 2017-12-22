package com.example.mike.mp3player.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.io.IOException;

/**
 * Created by Mike on 24/09/2017.
 */

public class MediaSessionCallback extends MediaSessionCompat.Callback {
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    private MediaSessionCompat mediaSession;
    private Context mContext;
    private MediaPlaybackService service;
    private MediaPlayerAdapter mediaPlayerAdapter;
    private MediaPlayerListener mediaPlayerListener;
    private MediaNotificationManager mMediaNotificationManager;

    public MediaSessionCallback(Context context, MediaSessionCompat mediaSession, MediaPlaybackService service) {
        this.mContext = context;
        this.service = service;
        this.mediaSession = mediaSession;
        this.mMediaNotificationManager = service.getmMediaNotificationManager();
        this.mediaPlayerListener = new MediaPlayerListener(mediaSession);
        this.mediaPlayerAdapter = new MediaPlayerAdapter(mContext, mediaPlayerListener);
        this.mediaPlayerListener.getmServiceManager().initServiceManager(this);
        this.afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
            }
        };
    }

    @Override
    public void onPlay() {
        playMedia(null);
    }

    @Override
    public void onPlayFromUri(Uri uri, Bundle extras)
    {
        // super.onPlayFromUri(uri, extras);
        playMedia(uri);
    }

    @Override
    public void onStop() {
//        unregisterReceiver(myNoisyAudioStreamReceiver);
        // Start the service
        mediaPlayerAdapter.onStop();
        service.stopSelf();
        // Set the session inactive  (and update metadata and state)
        getMediaSession().setActive(false);
        // stop the player (custom call)
//        player.stop();
        // Take the service out of the foreground
        service.stopForeground(false);
    }

    @Override
    public void onPause() {
        // Update metadata and state
        mediaPlayerAdapter.pause();

        // pause the player (custom call)
//        player.pause();
        // unregister BECOME_NOISY BroadcastReceiver
//        unregisterReceiver(myNoisyAudioStreamReceiver, intentFilter);
        // Take the service out of the foreground, retain the notification
        service.stopForeground(false);
    }

    @Override
    public void onPrepare() {
        if (!mediaSession.isActive()) {
            mediaSession.setActive(true);
        } // if session active
    } // onPrepare

    private void startService()
    {
        Intent startServiceIntent = new Intent(mContext, MediaPlaybackService.class);
        startServiceIntent.setAction("com.example.mike.mp3player.service.MediaPlaybackService");
        service.startService(startServiceIntent);
    }

    private void playMedia(Uri uri) {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for playback, this registers the afChangeListener
        int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start the service
            startService();

            if (null != uri) {
                mediaPlayerAdapter.playFromUri(uri);
            }

            // start the player (custom call)
            mediaPlayerAdapter.onPlay();
            getMediaSession().setActive(true);
//            // Register BECOME_NOISY BroadcastReceiver
//            registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
        }
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    public MediaPlayerAdapter getMediaPlayerAdapter() {
        return mediaPlayerAdapter;
    }

    public MediaPlayerListener getMediaPlayerListener() {
        return mediaPlayerListener;
    }

    public MediaNotificationManager getmMediaNotificationManager() {
        return mMediaNotificationManager;
    }

    public MediaPlaybackService getService() {
        return service;
    }
}
