package com.example.mike.mp3player.service.library.content.retriever;

import android.support.v4.media.MediaBrowserCompat;

import com.example.mike.mp3player.commons.MediaItemType;
import com.example.mike.mp3player.service.library.content.request.ContentRequest;

import java.util.List;

/**
 * Represents the fundamentals of a Content Retriever
 */
public abstract class ContentRetriever {
    /**
     * @param request the content request
     * @return a list of media items that match the content request
     */
    public abstract List<MediaBrowserCompat.MediaItem> getChildren(ContentRequest request);
    /**
     * @return The type of MediaItem retrieved from the Content Retriever
     */
    public abstract MediaItemType getType();
}
