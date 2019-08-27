package com.example.mike.mp3player.service.library.contentretriever;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;

import com.example.mike.mp3player.commons.MediaItemType;

import javax.annotation.Nullable;

import static com.example.mike.mp3player.commons.ComparatorUtils.uppercaseStringCompare;
import static com.example.mike.mp3player.commons.MediaItemUtils.getTitle;

public class SongsFromFolderRetriever extends ContentResolverRetriever {
    public SongsFromFolderRetriever(ContentResolver contentResolver, String idPrefix) {
        super(contentResolver, idPrefix);
    }

    @Override
    public MediaItemType getType() {
        return MediaItemType.SONG;
    }

    @Override
    Cursor performGetChildrenQuery(String id) {
        String WHERE_CLAUSE = MediaStore.Audio.Media.DATA + " LIKE ? ";
        String[] WHERE_ARGS = {id + "%"};
        return contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,getProjection(),
                WHERE_CLAUSE , WHERE_ARGS, null);
    }

    @Override
    String[] getProjection() {
        return SongsRetriever.PROJECTION;
    }

    @Override
    MediaBrowserCompat.MediaItem buildMediaItem(Cursor cursor, @Nullable String parentId) {
        return null;
    }

    @Override
    public int compare(MediaBrowserCompat.MediaItem m1, MediaBrowserCompat.MediaItem m2) {
        return uppercaseStringCompare(getTitle(m1), getTitle(m2));
    }
}
