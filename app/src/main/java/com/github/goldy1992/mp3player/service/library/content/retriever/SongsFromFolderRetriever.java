package com.github.goldy1992.mp3player.service.library.content.retriever;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;

import com.github.goldy1992.mp3player.commons.MediaItemType;
import com.github.goldy1992.mp3player.service.library.content.parser.SongResultsParser;
import com.github.goldy1992.mp3player.service.library.search.SearchDatabase;
import com.github.goldy1992.mp3player.service.library.search.Song;

import javax.inject.Inject;
import javax.inject.Named;

import static com.github.goldy1992.mp3player.service.library.content.Projections.SONG_PROJECTION;

public class SongsFromFolderRetriever extends ContentResolverRetriever<Song> {

    @Inject
    public SongsFromFolderRetriever(ContentResolver contentResolver,
                                    SongResultsParser resultsParser,
                                    SearchDatabase searchDatabase,
                                    @Named("worker") Handler handler) {
        super(contentResolver, resultsParser, searchDatabase.songDao(), handler);
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
        return SONG_PROJECTION.toArray(new String[0]);
    }

    @Override
    Song createFromMediaItem(@NonNull MediaBrowserCompat.MediaItem item) {
        return null;
    }

    @Override
    boolean isSearchable() {
        return false;
    }
}
