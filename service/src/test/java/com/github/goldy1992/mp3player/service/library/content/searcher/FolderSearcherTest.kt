package com.github.goldy1992.mp3player.service.library.content.searcher

import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import com.github.goldy1992.mp3player.commons.MediaItemType
import com.github.goldy1992.mp3player.service.library.MediaItemTypeIds
import com.github.goldy1992.mp3player.service.library.content.filter.FolderSearchResultsFilter
import com.github.goldy1992.mp3player.service.library.content.parser.FolderResultsParser
import com.github.goldy1992.mp3player.service.library.search.Folder
import com.github.goldy1992.mp3player.service.library.search.FolderDao
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class FolderSearcherTest : ContentResolverSearcherTestBase<FolderSearcher?>() {
    private lateinit var filter: FolderSearchResultsFilter
    @Mock
    var resultsParser: FolderResultsParser? = null
    private var mediaItemTypeIds: MediaItemTypeIds? = null
    @Mock
    var folderDao: FolderDao? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mediaItemTypeIds = MediaItemTypeIds()
        idPrefix = mediaItemTypeIds!!.getId(MediaItemType.FOLDER)
        filter = Mockito.mock(FolderSearchResultsFilter::class.java)
        Mockito.`when`(filter.filter(ContentResolverSearcherTestBase.Companion.VALID_QUERY, ContentResolverSearcherTestBase.Companion.expectedResult)).thenReturn(ContentResolverSearcherTestBase.Companion.expectedResult)
        searcher = Mockito.spy(FolderSearcher(contentResolver!!, resultsParser!!, filter, mediaItemTypeIds!!, folderDao!!))
    }

    @Test
    override fun testSearchValidMultipleArguments() {
        val expectedDbResult: MutableList<Folder?> = ArrayList()
        val id1 = "id1"
        val id2 = "id2"
        val id3 = "id3"
        val value1 = "value1"
        val value2 = "value2"
        val value3 = "value3"
        val folder1 = Folder(id1, value1)
        val folder2 = Folder(id2, value2)
        val folder3 = Folder(id3, value3)
        expectedDbResult.add(folder1)
        expectedDbResult.add(folder2)
        expectedDbResult.add(folder3)
        Mockito.`when`(folderDao!!.query(ContentResolverSearcherTestBase.Companion.VALID_QUERY)).thenReturn(expectedDbResult as List<Folder>)
        val EXPECTED_WHERE = (MediaStore.Audio.Media.DATA + " LIKE ? OR "
                + MediaStore.Audio.Media.DATA + " LIKE ? OR "
                + MediaStore.Audio.Media.DATA + " LIKE ? COLLATE NOCASE")
        val EXPECTED_WHERE_ARGS = arrayOf(
                searcher!!.likeParam(id1),
                searcher!!.likeParam(id2),
                searcher!!.likeParam(id3))
        Mockito.`when`(contentResolver!!.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, searcher!!.projection, EXPECTED_WHERE, EXPECTED_WHERE_ARGS, null))
                .thenReturn(cursor)
        Mockito.`when`<List<MediaBrowserCompat.MediaItem?>>(resultsParser!!.create(cursor!!, idPrefix!!)).thenReturn(ContentResolverSearcherTestBase.Companion.expectedResult)
        val result = searcher!!.search(ContentResolverSearcherTestBase.Companion.VALID_QUERY)
        Assert.assertEquals(ContentResolverSearcherTestBase.Companion.expectedResult, result)
    }

    @Test
    override fun testGetMediaType() {
        Assert.assertEquals(MediaItemType.FOLDERS, searcher!!.searchCategory)
    }
}