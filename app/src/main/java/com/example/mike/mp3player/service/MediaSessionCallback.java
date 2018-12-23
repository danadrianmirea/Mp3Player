package com.example.mike.mp3player.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import com.example.mike.mp3player.service.library.MediaLibrary;
import com.example.mike.mp3player.service.library.utils.MediaLibraryUtils;
import com.example.mike.mp3player.service.library.utils.ValidMetaDataUtil;

import java.util.List;

import static com.example.mike.mp3player.commons.Constants.DECREASE_PLAYBACK_SPEED;
import static com.example.mike.mp3player.commons.Constants.INCREASE_PLAYBACK_SPEED;
import static com.example.mike.mp3player.commons.Constants.ONE_SECOND;
import static com.example.mike.mp3player.commons.Constants.PLAYLIST;
import static com.example.mike.mp3player.commons.Constants.PLAY_ALL;
import static com.example.mike.mp3player.commons.Constants.UNKNOWN;
import static com.example.mike.mp3player.commons.MetaDataKeys.STRING_METADATA_KEY_ARTIST;

/**
 * Created by Mike on 24/09/2017.
 */

public class MediaSessionCallback extends MediaSessionCompat.Callback implements MediaPlayer.OnCompletionListener {

    private ServiceManager serviceManager;
    private PlaybackManager playbackManager;
    private MyMediaPlayerAdapter myMediaPlayerAdapter;
    private MediaSessionCompat mediaSession;
    private MyNotificationManager myNotificationManager;
    private MediaLibrary mediaLibrary;
    private ReceiveBroadcasts broadcastReceiver;
    private Context context;
    private static final String LOG_TAG = "MEDIA_SESSION_CALLBACK";

    public MediaSessionCallback(Context context, MyNotificationManager myNotificationManager,
                                ServiceManager serviceManager, MediaSessionCompat mediaSession,
                                MediaLibrary mediaLibrary) {
        this.serviceManager = serviceManager;
        this.mediaSession = mediaSession;
        this.mediaLibrary = mediaLibrary;
        this.myNotificationManager = myNotificationManager;
        this.playbackManager = new PlaybackManager();
        this.myMediaPlayerAdapter = new MyMediaPlayerAdapter(context);
        this.broadcastReceiver = new ReceiveBroadcasts();
        this.context = context;
    }

    public void init() {
        List<MediaSessionCompat.QueueItem> queueItems =  MediaLibraryUtils.convertMediaItemsToQueueItem(this.mediaLibrary.getLibrary());
        this.playbackManager.init(queueItems);

        Uri firstSongUri = this.mediaLibrary.getMediaUri(playbackManager.selectFirstItem());
        this.myMediaPlayerAdapter.init(firstSongUri);
        this.myMediaPlayerAdapter.getMediaPlayer().setOnCompletionListener(this);
        updateMediaSession();
    }

    @Override
    public void onPlay() {
        broadcastReceiver.registerAudioNoisyReceiver();
        myMediaPlayerAdapter.play();
        updateMediaSession();
        serviceManager.startService(prepareNotification());
    }

    @Override
    public void onSkipToNext() {
        String newMediaId = playbackManager.skipToNext();
        skipToNewMedia(newMediaId);
        serviceManager.notify(prepareNotification());
    }

    @Override
    public void onSkipToPrevious() {
        int position = myMediaPlayerAdapter.getCurrentTrackPosition();
        String newMediaId = position > ONE_SECOND ? playbackManager.getCurrentMediaId() :  playbackManager.skipToPrevious();;
        skipToNewMedia(newMediaId);
        serviceManager.notify(prepareNotification());
    }
    @Override
    public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        if (mediaButtonEvent != null && mediaButtonEvent.getExtras() != null
                && mediaButtonEvent.getExtras().getParcelable(Intent.EXTRA_KEY_EVENT) != null) {
            KeyEvent keyEvent = mediaButtonEvent.getExtras().getParcelable(Intent.EXTRA_KEY_EVENT);
            int keyEventCode = keyEvent.getKeyCode();

            switch (keyEventCode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY: onPlay(); break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE: onPause(); break;
                case KeyEvent.KEYCODE_MEDIA_NEXT: onSkipToNext(); break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS: onSkipToPrevious(); break;
                default: break;
            }
            return true;
        }
        return false;

    }

    @Override
    public void onPrepareFromUri(Uri uri, Bundle bundle) {
        super.onPrepareFromUri(uri, bundle);
        myMediaPlayerAdapter.prepareFromUri(uri);
        updateMediaSession();
    }

    @Override
    public void onPrepareFromMediaId(String mediaId, Bundle bundle) {
        super.onPrepareFromMediaId(mediaId, bundle);

        if (bundle.containsKey(PLAYLIST)) {
              if (bundle.getString(PLAYLIST).equals(PLAY_ALL)) {
                  playbackManager.createNewPlaylist(MediaLibraryUtils.convertMediaItemsToQueueItem(mediaLibrary.getLibrary()));
              }
        }
        Uri uri = mediaLibrary.getMediaUri(mediaId);
        myMediaPlayerAdapter.prepareFromUri(uri);
        playbackManager.setQueueIndex(mediaId);
        updateMediaSession();
    }

    private void updateMediaSession() {
        mediaSession.setPlaybackState(myMediaPlayerAdapter.getMediaPlayerState());
        mediaSession.setMetadata(getCurrentMetaData());
    }

    @Override
    public void onPlayFromUri(Uri uri, Bundle bundle) {
        super.onPlayFromUri(uri, bundle);
        myMediaPlayerAdapter.prepareFromUri(uri);
        myMediaPlayerAdapter.play();
        broadcastReceiver.registerAudioNoisyReceiver();

        serviceManager.startMediaSession();
        updateMediaSession();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        broadcastReceiver.unregisterAudioNoisyReceiver();
        myMediaPlayerAdapter.pause();
        // unregister BECOME_NOISY BroadcastReceiver
//        unregisterReceiver(myNoisyAudioStreamReceiver, intentFilter);
        // Take the serviceManager out of the foreground, retain the notification
        updateMediaSession();
        serviceManager.pauseService(prepareNotification());
    }

    @Override
    public void onSeekTo(long position ) {
        myMediaPlayerAdapter.seekTo(position);
        mediaSession.setPlaybackState(myMediaPlayerAdapter.getMediaPlayerState());
    }

    @Override
    public void onAddQueueItem(MediaDescriptionCompat description) {
        MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(description, description.hashCode());
        mediaSession.setQueue(playbackManager.onAddQueueItem(item));
    }

    @Override
    public void onRemoveQueueItem(MediaDescriptionCompat description) {
        MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(description, description.hashCode());
        mediaSession.setQueue(playbackManager.onRemoveQueueItem(item));
    }

    private Notification prepareNotification() {
        return myNotificationManager.getNotification(getCurrentMetaData(),
                myMediaPlayerAdapter.getMediaPlayerState(),
                mediaSession.getSessionToken());
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Uri nextItemUri = mediaLibrary.getMediaUri(playbackManager.playbackComplete());
        myMediaPlayerAdapter.prepareFromUri(nextItemUri);
        myMediaPlayerAdapter.play();
        updateMediaSession();
    }

    @Override
    public void onCustomAction(String customAction, Bundle extras) {
        super.onCustomAction(customAction, extras);
        switch (customAction) {
            case INCREASE_PLAYBACK_SPEED: myMediaPlayerAdapter.increaseSpeed(0.05f);
            break;
            case DECREASE_PLAYBACK_SPEED: myMediaPlayerAdapter.decreaseSpeed(0.05f);
            break;
            default: break;
        }
        updateMediaSession();
    }

    private MediaMetadataCompat getCurrentMetaData() {
        MediaMetadataCompat.Builder builder = myMediaPlayerAdapter.getCurrentMetaData();
        MediaSessionCompat.QueueItem currentItem = playbackManager.getCurrentItem();
        if (ValidMetaDataUtil.validTitle(currentItem)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentItem.getDescription().getTitle().toString());
        } else {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, UNKNOWN);
        }

        if (ValidMetaDataUtil.validArtist(currentItem)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentItem.getDescription().getExtras().getString(STRING_METADATA_KEY_ARTIST));
        } else {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, UNKNOWN);
        }
        return builder.build();
    }

    private void skipToNewMedia(String newMediaId) {
        Uri newUri = mediaLibrary.getMediaUri(newMediaId);
        PlaybackStateCompat currentState = myMediaPlayerAdapter.getMediaPlayerState();
        myMediaPlayerAdapter.prepareFromUri(newUri);
        if (currentState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            myMediaPlayerAdapter.play();
        }
        updateMediaSession();
    }


    private class ReceiveBroadcasts extends BroadcastReceiver {
        private boolean audioNoisyReceiverRegistered;
        private final IntentFilter AUDIO_NOISY_INTENT_FILTER =
                new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (myMediaPlayerAdapter.isPlaying()) {
                    myMediaPlayerAdapter.pause();
                    updateMediaSession();
                }
            }
        }

        public void registerAudioNoisyReceiver() {
            if (!audioNoisyReceiverRegistered) {
                context.registerReceiver(this, AUDIO_NOISY_INTENT_FILTER);
                audioNoisyReceiverRegistered = true;
            }
        }

        public void unregisterAudioNoisyReceiver() {
            if (audioNoisyReceiverRegistered) {
                context.unregisterReceiver(this);
                audioNoisyReceiverRegistered = false;
            }
        }
    }
}