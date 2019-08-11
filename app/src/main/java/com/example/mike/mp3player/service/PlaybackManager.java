package com.example.mike.mp3player.service;

import static android.support.v4.media.MediaBrowserCompat.MediaItem;

import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.VisibleForTesting;

import com.example.mike.mp3player.service.library.utils.MediaLibraryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.inject.Inject;

public class PlaybackManager {

    private static final int START_OF_PLAYLIST = 0;
    private static final int EMPTY_PLAYLIST_INDEX = -1;
    private Stack<Integer> shufflePreviousStack = new Stack<>();
    private Stack<Integer> shuffleNextStack = new Stack<>();
    private int nextShuffledIndex = 0;
    private int queueIndex = EMPTY_PLAYLIST_INDEX;
    private Random random = new Random();
    private boolean isRepeating = true;
    private final List<MediaItem> playlist = new ArrayList<>();
    private boolean shuffleOn = false;

    @Inject
    public PlaybackManager() { }

    public List<MediaItem> onAddQueueItem(MediaItem item) {
        playlist.add(item);
        queueIndex = (queueIndex == -1) ? 0 : queueIndex;
        return playlist;
    }

    public List<MediaItem> onRemoveQueueItem(MediaItem item) {
        boolean removed = playlist.remove(item);
        if (removed) {
            queueIndex = (playlist.isEmpty()) ? -1 : queueIndex;
        }
        return playlist;
    }

    public void notifyPlaybackComplete() {
        if (isShuffleOn()) {
            shufflePreviousStack.push(queueIndex);
            if (shuffleNextStack.empty()) {
                queueIndex = nextShuffledIndex;
                nextShuffledIndex = getNextShuffledIndex();
            } else {
                queueIndex = shuffleNextStack.pop();
            }
        } else {
            queueIndex++;
            if (isRepeating && (queueIndex >= playlist.size())) {
                queueIndex = START_OF_PLAYLIST;
            }
        }
    }

    public Uri getNext() {
        int newIndex;
        if (isShuffleOn()) {
            if (shuffleNextStack.empty()) {
                newIndex =  nextShuffledIndex;
            } else {
                newIndex = shuffleNextStack.peek();
            }
        } else {
            newIndex = getNextMediaItemIndex();
        }
        return getPlaylistMediaUri(newIndex);
    }

    public Uri skipToNext() {
        if (isShuffleOn()) {
            shufflePreviousStack.push(new Integer(queueIndex));
            if (shuffleNextStack.empty()) {
                Uri toReturn = getPlaylistMediaUri(this.nextShuffledIndex);
                this.queueIndex = this.nextShuffledIndex;
                this.nextShuffledIndex = getNextShuffledIndex();
                return toReturn;
            } else {
                this.queueIndex = shuffleNextStack.pop();
                return  getPlaylistMediaUri(this.queueIndex);
            }
        } else {
            return incrementQueue() ? getCurrentMediaUri() : null;
        }
    }

    public Uri skipToPrevious() {
        if (isShuffleOn()) {
            if (shufflePreviousStack.empty()) { // there's no previous available so keep same track
                return getPlaylistMediaUri(queueIndex);
            } else { // get prevo=ious and add to the next stack;
                shuffleNextStack.push(new Integer(queueIndex));
                queueIndex = shufflePreviousStack.pop();
                return getPlaylistMediaUri(queueIndex);
            }
        } else {
            return decrementQueue() ? getCurrentMediaUri() : null;
        }
    }

    private boolean incrementQueue() {
        int newIndex = getNextMediaItemIndex();
        if (validQueueIndex(newIndex)) {
            this.queueIndex = newIndex;
            return true;
        }
        return false;
    }

    private boolean decrementQueue() {
        int newIndex = getPreviousMediaItemIndex();
        if (validQueueIndex(newIndex)) {
            this.queueIndex = newIndex;
            return true;
        }
        return false;
    }
    /**
     * ASSUMES shuffleON IS FALSE
     * @return
     */
    private int getNextMediaItemIndex() {
        int newIndex = queueIndex + 1;
        boolean passedEndOfQueue = newIndex >= playlist.size();
        if (passedEndOfQueue) {
            return isRepeating ? START_OF_PLAYLIST : -1;
        }
        return newIndex;
    }
    /**
     * ASSUMES shuffleON IS FALSE
     * @return
     */
    private int getPreviousMediaItemIndex() {
        int newIndex = queueIndex - 1;
        boolean beforeStartOfQueue = newIndex < START_OF_PLAYLIST;
        if (beforeStartOfQueue) {
            return isRepeating ? getLastIndex() : -1;
        }
        return newIndex;
    }

    public boolean validQueueIndex(int newQueueIndex) {
        return newQueueIndex < playlist.size() && newQueueIndex >= 0;
    }

    public Uri getPlaylistMediaUri(int index) {
        if (validQueueIndex(index)) {
            MediaItem queueItem = playlist.get(index);
            if (queueItem != null) {
                return playlist.get(index).getDescription().getMediaUri();
            }
        }
        return null;
    }

    public Uri getCurrentMediaUri() {
        if (playlist != null && !playlist.isEmpty() && playlist.get(queueIndex) != null) {
            MediaItem currentItem = playlist.get(queueIndex);
            if (currentItem.getDescription() != null) {
                return  currentItem.getDescription().getMediaUri();
            }
        }
        return null;
    }

    public boolean createNewPlaylist(List<MediaItem> newList) {
        playlist.clear();
        boolean result = playlist.addAll(newList);
        queueIndex = playlist.isEmpty() ?  EMPTY_PLAYLIST_INDEX : START_OF_PLAYLIST;
        return result;
    }

    public void setCurrentItem(String mediaId) {
        Integer integerQueueIndex = MediaLibraryUtils.findIndexOfTrackInPlaylist(playlist, mediaId);
        if (integerQueueIndex == null) {
            queueIndex = -1;
        } else {
            queueIndex = integerQueueIndex;
        }
    }

    public MediaItem getCurrentItem() {
        if (validQueueIndex(queueIndex)) {
            return playlist.get(queueIndex);
        }
        return  null;
    }

    private int getNextShuffledIndex() {
        this.nextShuffledIndex = shuffleNextStack.empty() ? shuffleNewIndex() : shuffleNextStack.pop();
        return this.nextShuffledIndex;
    }

    @VisibleForTesting()
    public int shuffleNewIndex() {
        return random.nextInt(getQueueSize());
    }

    public void setShuffle(boolean shuffleOn) {
        if (shuffleOn == this.isShuffleOn()) {
            return;
        }
        this.shuffleOn = shuffleOn;
        if (shuffleOn) {
            this.shuffleNextStack.clear();
            this.shufflePreviousStack.clear();
            this.nextShuffledIndex = shuffleNewIndex();
        }
    }

    @PlaybackStateCompat.ShuffleMode
    public int getShuffleMode() {
        if (shuffleOn)
        {
            return PlaybackStateCompat.SHUFFLE_MODE_ALL;
        }
        return PlaybackStateCompat.SHUFFLE_MODE_NONE;
    }

    public int getLastIndex() {
        return playlist.size() - 1;
    }

    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }

    public int getQueueSize() {
        return playlist.size();
    }

    public boolean isShuffleOn() { return shuffleOn; }

    public boolean isInitialised() {
        return !playlist.isEmpty();
    }
}
