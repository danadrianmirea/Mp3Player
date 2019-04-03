package com.example.mike.mp3player.client.views.fragments.viewpager;

import android.content.Intent;

import com.example.mike.mp3player.client.MediaBrowserAdapter;
import com.example.mike.mp3player.client.MediaControllerAdapter;
import com.example.mike.mp3player.client.MyGenericItemTouchListener;
import com.example.mike.mp3player.client.activities.MediaPlayerActivity;
import com.example.mike.mp3player.client.utils.IntentUtils;
import com.example.mike.mp3player.commons.ComparatorUtils;
import com.example.mike.mp3player.commons.MediaItemUtils;
import com.example.mike.mp3player.commons.library.Category;
import com.example.mike.mp3player.commons.library.LibraryObject;
import com.example.mike.mp3player.commons.library.LibraryRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import androidx.annotation.NonNull;

import static android.support.v4.media.MediaBrowserCompat.MediaItem;

public class SongViewPageFragment extends GenericViewPageFragment implements MyGenericItemTouchListener.ItemSelectedListener {

    private static final String LOG_TAG = "VIEW_PAGE_FRAGMENT";

    public SongViewPageFragment() {  }

    public void init(LibraryRequest libraryObject, List<MediaItem> songs, MediaBrowserAdapter mediaBrowserAdapter,
                     MediaControllerAdapter mediaControllerAdapter) {
        super.init(Category.SONGS, songs, mediaBrowserAdapter, mediaControllerAdapter, MediaPlayerActivity.class);
        this.parent = libraryObject;
        this.songs = new TreeMap<>(ComparatorUtils.compareMediaItemsByTitle);
        for (MediaItem m : songs) {
            this.songs.put(m, null);
        }
    }


    public static SongViewPageFragment createAndInitialiseViewPageFragment(LibraryRequest libraryObject, List<MediaItem> songs, MediaBrowserAdapter mediaBrowserAdapter,
                                                                           MediaControllerAdapter mediaControllerAdapter) {
        SongViewPageFragment viewPageFragment = new SongViewPageFragment();
        viewPageFragment.init(libraryObject, songs, mediaBrowserAdapter, mediaControllerAdapter);
        return viewPageFragment;
    }

    /**
     * @deprecated must specify category in order to know which type of request to build
     * @param songs
     * @param mediaBrowserAdapter
     * @param mediaControllerAdapter
     * @return
     */
    @Deprecated
    public static SongViewPageFragment createAndInitialiseViewPageFragment(List<MediaItem> songs, MediaBrowserAdapter mediaBrowserAdapter,
                                                                           MediaControllerAdapter mediaControllerAdapter) {
        SongViewPageFragment viewPageFragment = new SongViewPageFragment();
        viewPageFragment.init(Category.SONGS, songs, mediaBrowserAdapter, mediaControllerAdapter, MediaPlayerActivity.class);
        return viewPageFragment;
    }

    @Override
    public void onChildrenLoaded(LibraryRequest libraryObject, @NonNull ArrayList<MediaItem> children) {
        // not needed
    }

    @Override
    public void itemSelected(MediaItem item) {
        String mediaId = MediaItemUtils.getMediaId(item);
        if (null == mediaId) {
            return;
        }
//        Intent intent =
//                IntentUtils.createMediaPlayerActivityMediaRequestIntent(context,
//                        getMediaBrowserAdapter().getMediaSessionToken(),mediaId, parent);
//
//        startActivity(intent);
    }
}

