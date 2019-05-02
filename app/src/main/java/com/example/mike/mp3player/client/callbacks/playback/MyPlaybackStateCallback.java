package com.example.mike.mp3player.client.callbacks.playback;

import android.os.Looper;
import android.support.v4.media.session.PlaybackStateCompat;

import com.example.mike.mp3player.client.callbacks.AsyncCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.mike.mp3player.commons.Constants.NO_ACTION;

public class MyPlaybackStateCallback extends AsyncCallback<PlaybackStateCompat> {
    private static final String LOG_TAG = "MY_PLYBK_ST_CLLBK";
    private Map<ListenerType, Set<PlaybackStateListener>> listeners;

    public MyPlaybackStateCallback(Looper looper) {
        super(looper);
        this.listeners = new HashMap<>();
    }

    public void updateAll(PlaybackStateCompat state) {
        for (ListenerType listenerType : listeners.keySet()) {
            notifyListenersOfType(listenerType, state);
        }
    }

    @Override
    public void processCallback(PlaybackStateCompat data) {
        // TODO: make use of the actions in state to direct updates to the relevant listeners only
        //logPlaybackStateCompat(data, LOG_TAG);
        long actions = data.getActions();

        if (actions == NO_ACTION) {
            notifyListenersOfType(ListenerType.PLAYBACK, data);
        }

        if (containsAction(actions, PlaybackStateCompat.ACTION_SET_REPEAT_MODE)) {
            notifyListenersOfType(ListenerType.REPEAT, data);
        }

        if (containsAction(actions, PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE)) {
            notifyListenersOfType(ListenerType.SHUFFLE, data);
        }
    }

    public synchronized void registerPlaybackStateListener(PlaybackStateListener listener, Set<ListenerType> listenerTypes) {
        for (ListenerType listenerType : listenerTypes) {
            if (!listeners.containsKey(listenerType)) {
                listeners.put(listenerType, new HashSet<>());
            }
            listeners.get(listenerType).add(listener);
        }
        //Log.i(LOG_TAG, "registerPlaybackListener" + listener.getClass());
    }


    public synchronized boolean removePlaybackStateListener(PlaybackStateListener listener, ListenerType listenerType) {
        boolean result = false;
        if (listeners.containsKey(listenerType)) {
            Set<PlaybackStateListener> listenersValue = listeners.get(listenerType);
            result = listenersValue.remove(listener);
            if (listenersValue.isEmpty()) {
                listeners.remove(listenerType);
            }
        }
        return result;
    }

    private boolean containsAction(long actions, long action) {
        return (actions & action) != 0;
    }

    private void notifyListenersOfType(ListenerType listenerType, PlaybackStateCompat state) {
        if (null != state) {
            Set<PlaybackStateListener> listenersSet = listeners.get(listenerType);
            StringBuilder sb = new StringBuilder();
            if (listenersSet != null) {
                for (PlaybackStateListener listener : listenersSet) {
                    listener.onPlaybackStateChanged(state);
                    sb.append(listener.getClass());
                }
            }
        }
        //Log.i(LOG_TAG, "hit playback state changed with status " + Constants.playbackStateDebugMap.get(state.getState()) + ", listeners " + listenersSet.size() + ", " + sb.toString());
    }
}
