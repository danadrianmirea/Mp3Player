package com.example.mike.mp3player.commons;

import android.support.v4.media.MediaDescriptionCompat;

import com.example.mike.mp3player.commons.library.Category;

import org.junit.jupiter.api.Test;

import static android.support.v4.media.MediaBrowserCompat.MediaItem;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparatorUtilsTest {
    /** An id constant used for testing */
    private static final String MISC_STRING = "xyz";
    /** String to use for a greater than string */
    private static final String GREATER_STRING = "z_GREATER_STRING";
    /** String to use for a less than string */
    private static final String LESSER_STRING = "a_LESSER_STRING";
    /**
     * SONGS_ROOT
     */
    private static final MediaItem SONGS_ROOT = createRootItem(Category.SONGS);
    /**
     * ALBUMS_ROOT
     */
    private static final MediaItem ALBUMS_ROOT = createRootItem(Category.ALBUMS);
    /**
     *
     */
    @Test
    public void testCompareRootItemsByCategoryGreaterAndLessThan() {
        int result = ComparatorUtils.compareRootMediaItemsByCategory.compare(SONGS_ROOT, ALBUMS_ROOT);
        final boolean albumsGreaterThanZero = result < 0;
        assertTrue(albumsGreaterThanZero);
        result = ComparatorUtils.compareRootMediaItemsByCategory.compare(ALBUMS_ROOT, SONGS_ROOT);
        final boolean songsLessThanAlbums = result > 0;
        assertTrue(songsLessThanAlbums);
    }
    /**
     *
     */
    @Test
    public void testCompareRootItemsByCategoryNullAgainstCategory() {
        final MediaItem nullCategory = createMediaItemWithNullCategory();
        final int result = ComparatorUtils.compareRootMediaItemsByCategory.compare(SONGS_ROOT, nullCategory);
        final boolean songsGreaterThanNull = result > 0;
        assertTrue(songsGreaterThanNull);
    }
    /**
     *
     */
    @Test
    public void testCompareRootItemsEqual() {
        final MediaItem songsRootCopy = createRootItem(Category.SONGS);
        final int result = ComparatorUtils.compareRootMediaItemsByCategory.compare(SONGS_ROOT, songsRootCopy);
        final boolean songsGreaterThanNull = result == 0;
        assertTrue(songsGreaterThanNull);
    }
    /**
     * 
     */
    @Test
    public void testCompareMediaItemByTitleGreaterAndLessThan() {
        final MediaItem greaterTitleMediaItem = createMediaItem(MISC_STRING, GREATER_STRING, MISC_STRING);
        final MediaItem lesserTitleMediaItem = createMediaItem(MISC_STRING, LESSER_STRING, MISC_STRING);
        int result = ComparatorUtils.compareMediaItemsByTitle.compare(greaterTitleMediaItem, lesserTitleMediaItem);
        assertTrue( result > 0);
        result = ComparatorUtils.compareMediaItemsByTitle.compare(lesserTitleMediaItem, greaterTitleMediaItem);
        assertTrue( result < 0);
    }
    /**
     *
     */
    @Test
    public void testCompareMediaItemByTitleAgainstNull() {
        final MediaItem greaterTitleMediaItem = createMediaItem(MISC_STRING, GREATER_STRING, MISC_STRING);
        final MediaItem nullTitleMediaItem = createMediaItem(MISC_STRING, null, MISC_STRING);
        int result = ComparatorUtils.compareMediaItemsByTitle.compare(greaterTitleMediaItem, nullTitleMediaItem);
        assertTrue( result > 0);
    }
    /**
     *
     */
    @Test
    public void testCompareMediaItemByTitleEqual() {
        final MediaItem mediaItem = createMediaItem(MISC_STRING, MISC_STRING, MISC_STRING);
        final MediaItem mediaItemSameId = createMediaItem(MISC_STRING, MISC_STRING, MISC_STRING);
        final int result = ComparatorUtils.compareMediaItemById.compare(mediaItem, mediaItemSameId);
        final boolean EQUAL = result == 0;
        assertTrue(EQUAL);
    }
    /**
     *
     */
    @Test
    public void testCompareMediaItemByIdGreaterAndLessThan() {
        final MediaItem greaterTitleMediaItem = createMediaItem(GREATER_STRING, MISC_STRING, MISC_STRING);
        final MediaItem lesserTitleMediaItem = createMediaItem(LESSER_STRING, MISC_STRING, MISC_STRING);
        int result = ComparatorUtils.compareMediaItemById.compare(greaterTitleMediaItem, lesserTitleMediaItem);
        assertTrue( result > 0);
        result = ComparatorUtils.compareMediaItemById.compare(lesserTitleMediaItem, greaterTitleMediaItem);
        assertTrue( result < 0);
    }
    /**
     *
     */
    @Test
    public void testCompareMediaItemByIdAgainstNull() {
        final MediaItem mediaItemNullId = createMediaItem(null, MISC_STRING, MISC_STRING);
        final MediaItem mediaItem = createMediaItem(LESSER_STRING, MISC_STRING, MISC_STRING);
        int result = ComparatorUtils.compareMediaItemById.compare(mediaItemNullId, mediaItem);
        assertTrue( result < 0);
    }
    /**
     *
     */
    @Test
    public void testCompareMediaItemByIdEqualIds() {
        final MediaItem mediaItem = createMediaItem(MISC_STRING, GREATER_STRING, MISC_STRING);
        final MediaItem mediaItemSameId = createMediaItem(MISC_STRING, LESSER_STRING, MISC_STRING);
        final int result = ComparatorUtils.compareMediaItemById.compare(mediaItem, mediaItemSameId);
        final boolean EQUAL = result == 0;
        assertTrue(EQUAL);
    }
    /**
     * @return a root category item
     */
    private static MediaItem createRootItem(Category category) {
        MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat.Builder()
                .setDescription(category.getDescription())
                .setTitle(category.getTitle())
                .setMediaId(category.name())
                .build();
        return new MediaItem(mediaDescriptionCompat, 0);
    }
    /**
     * @return a null category MediaItem
     */
    private static MediaItem createMediaItemWithNullCategory() {
       return createMediaItem(null, null, null);
    }
    /**
     * Utility class to make a media item
     * @param id the id
     * @param title the title
     * @param description the description
     * @return a new MediaItem
     */
    private static MediaItem createMediaItem(final String id, final String title, final String description) {
        MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat.Builder()
                .setDescription(description)
                .setTitle(title)
                .setMediaId(id)
                .build();
        return new MediaItem(mediaDescriptionCompat, 0);
    }
}