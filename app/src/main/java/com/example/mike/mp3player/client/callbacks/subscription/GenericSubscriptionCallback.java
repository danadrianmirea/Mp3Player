package com.example.mike.mp3player.client.callbacks.subscription;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import com.example.mike.mp3player.client.MediaBrowserAdapter;
import com.example.mike.mp3player.client.MediaBrowserResponseListener;
import com.example.mike.mp3player.commons.library.LibraryResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

import static com.example.mike.mp3player.commons.Constants.RESPONSE_OBJECT;

public abstract class GenericSubscriptionCallback<K> extends MediaBrowserCompat.SubscriptionCallback {
    public abstract SubscriptionType getType();
    private static final String LOG_TAG = "SUBSCRIPTION_CALLBACK";
    Handler handler;
    Map<K, Set<MediaBrowserResponseListener>> mediaBrowserResponseListeners;
    Context context;
    MediaBrowserAdapter mediaBrowserAdapter;

    public GenericSubscriptionCallback(MediaBrowserAdapter mediaBrowserAdapter) {
        super();
        this.context = mediaBrowserAdapter.getContext();
        this.mediaBrowserResponseListeners = new HashMap<>();
        this.handler = new Handler(mediaBrowserAdapter.getLooper());
        this.mediaBrowserAdapter = mediaBrowserAdapter;
    }

    @Override
    public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
        onChildrenLoaded(parentId, children, null);
    }

    @Override
    public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children,
                                 @NonNull Bundle options) {
        super.onChildrenLoaded(parentId, children, options);

        LibraryResponse libraryRequest = (LibraryResponse) options.get(RESPONSE_OBJECT);
        if (null == libraryRequest) {
            Log.e(LOG_TAG, getClass() + " onLoadChildren, did not contain a library id in " +
                    "the options bundle");
            return;
        }


    }

    public synchronized void registerMediaBrowserResponseListener(K key , MediaBrowserResponseListener listener) {
        if (mediaBrowserResponseListeners.get(key) == null) {
            mediaBrowserResponseListeners.put(key, new HashSet<>());
        }
        mediaBrowserResponseListeners.get(key).add(listener);
    }

    public synchronized void registerMediaBrowserResponseListeners(K key, Collection<MediaBrowserResponseListener> listeners) {
        if (mediaBrowserResponseListeners.get(key) == null) {
            mediaBrowserResponseListeners.put(key, new HashSet<>());
        }
        mediaBrowserResponseListeners.get(key).addAll(listeners);
    }

    public synchronized boolean removeMediaBrowserResponseListener(K key, MediaBrowserResponseListener listener) {
        return mediaBrowserResponseListeners.get(key).remove(listener);
    }
}
