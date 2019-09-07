package com.example.mike.mp3player.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;

import com.example.mike.mp3player.R;
import com.example.mike.mp3player.client.callbacks.subscription.SubscriptionType;
import com.example.mike.mp3player.client.views.fragments.PlayToolBarFragment;
import com.example.mike.mp3player.client.views.fragments.SimpleTitleBarFragment;
import com.example.mike.mp3player.client.views.fragments.viewpager.ChildViewPagerFragment;
import com.example.mike.mp3player.commons.MediaItemType;
import com.example.mike.mp3player.commons.MediaItemUtils;

import javax.inject.Inject;

import static com.example.mike.mp3player.commons.Constants.LIBRARY_ID;
import static com.example.mike.mp3player.commons.Constants.MEDIA_ID;
import static com.example.mike.mp3player.commons.Constants.MEDIA_ITEM;
import static com.example.mike.mp3player.commons.Constants.MEDIA_ITEM_TYPE;
import static com.example.mike.mp3player.commons.Constants.MEDIA_ITEM_TYPE_ID;
import static com.example.mike.mp3player.commons.Constants.PARENT_MEDIA_ITEM_TYPE;
import static com.example.mike.mp3player.commons.Constants.PARENT_MEDIA_ITEM_TYPE_ID;
import static com.example.mike.mp3player.commons.Constants.REQUEST_OBJECT;

public abstract class FolderActivity extends MediaActivityCompat {

    private static final String LOG_TAG = "FOLDER_ACTIVITY";
    private ChildViewPagerFragment viewPageFragment;
    private PlayToolBarFragment playToolBarFragment;
    private SimpleTitleBarFragment simpleTitleBarFragment;

    @Override
    SubscriptionType getSubscriptionType() {
        return SubscriptionType.MEDIA_ID;
    }

    @Override
    String getWorkerId() {
        return "FLDER_ACTVY_WKR";
    }

    @Override
    boolean initialiseView(int layoutId) {
        setContentView(layoutId);
        this.simpleTitleBarFragment = (SimpleTitleBarFragment) getSupportFragmentManager().findFragmentById(R.id.simpleTitleBarFragment);
        this.playToolBarFragment = (PlayToolBarFragment) getSupportFragmentManager().findFragmentById(R.id.playToolbarFragment);
        return true;
    }

    @Override
    public void onConnected() {
        super.onConnected();
        MediaItem mediaItem = getIntent().getParcelableExtra(MEDIA_ITEM);
        String itemLibraryId = MediaItemUtils.getLibraryId(mediaItem);
        this.viewPageFragment.init(MediaItemType.FOLDER, itemLibraryId);
        initialiseView(R.layout.activity_folder);
        getSupportFragmentManager().beginTransaction().add(R.id.songListFragment, viewPageFragment).commit();
        getSupportActionBar().setTitle(MediaItemUtils.getTitle(mediaItem));
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Inject
    public void setViewPageFragment(ChildViewPagerFragment childViewPagerFragment) {
        this.viewPageFragment = childViewPagerFragment;
    }
}
