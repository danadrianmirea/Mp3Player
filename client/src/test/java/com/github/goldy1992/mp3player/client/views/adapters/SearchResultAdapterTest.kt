package com.github.goldy1992.mp3player.client.views.adapters

import android.support.v4.media.MediaBrowserCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry
import com.github.goldy1992.mp3player.client.views.viewholders.EmptyListViewHolder
import com.github.goldy1992.mp3player.client.views.viewholders.MyFolderViewHolder
import com.github.goldy1992.mp3player.client.views.viewholders.MySongViewHolder
import com.github.goldy1992.mp3player.client.views.viewholders.RootItemViewHolder
import com.github.goldy1992.mp3player.commons.MediaItemBuilder
import com.github.goldy1992.mp3player.commons.MediaItemType
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SearchResultAdapterTest : MediaItemRecyclerViewAdapterTestBase() {

    private var searchResultAdapter: SearchResultAdapter? = null

    @Before
    override fun setup() {
        super.setup()
        context = InstrumentationRegistry.getInstrumentation().context
        searchResultAdapter = SearchResultAdapter(albumArtPainter)
    }

    /**
     * Test creation of RootItemViewHolder
     */
    @Test
    fun testOnCreateViewHolderRootItem() {
        assertCreatedViewItem(RootItemViewHolder::class.java, MediaItemType.ROOT.value)
    }

    /**
     * Test creation of SongItemViewHolder
     */
    @Test
    fun testOnFolderViewHolderSongItem() {
        assertCreatedViewItem(MySongViewHolder::class.java, MediaItemType.SONG.value)
    }

    @Test
    fun testOnEmptyViewHolderSongItem() {
        assertCreatedViewItem(EmptyListViewHolder::class.java, -1)
    }

    /**
     * Test creation of FolderItemViewHolder
     */
    @Test
    fun testOnFolderViewHolderFolderItem() {
        assertCreatedViewItem(MyFolderViewHolder::class.java, MediaItemType.FOLDER.value)
    }

    /**  */
    @Test
    fun testBindViewHolder() {
        val mySongViewHolder = Mockito.mock(MySongViewHolder::class.java)
        mediaItems.add(
                MediaItemBuilder("101")
                        .build()
        )
        searchResultAdapter!!.items.addAll(mediaItems!!)
        searchResultAdapter!!.onBindViewHolder(mySongViewHolder, 0)
    }

    @Test
    fun testGetItemViewType() {
        val mediaItemType = MediaItemType.FOLDER
        val mediaItem = MediaItemBuilder("id")
                .setMediaItemType(mediaItemType)
                .build()
        searchResultAdapter!!.items.add(mediaItem) // add as the first item
        val result = searchResultAdapter!!.getItemViewType(0)
        Assert.assertEquals(mediaItemType.value.toLong(), result.toLong())
    }

    @Test
    fun testGetItemViewTypeNoMediaItemType() {
        val mediaItem = MediaItemBuilder("id")
                .build()
        searchResultAdapter!!.items.add(mediaItem) // add as the first item
        val result = searchResultAdapter!!.getItemViewType(0)
        Assert.assertEquals(0, result.toLong())
    }

    @Test
    fun testItemCount() {
        val mediaItem = Mockito.mock(MediaBrowserCompat.MediaItem::class.java)
        val expectedSize = 5
        for (i in 1..5) {
            mediaItems.add(mediaItem)
        }
        searchResultAdapter!!.items.addAll(mediaItems!!)
        Assert.assertEquals(expectedSize.toLong(), searchResultAdapter!!.itemCount.toLong())
    }

    /**
     *
     * @param viewHolderType the view holder class type
     * @param viewType the view type code
     * @param <T> The type of ViewHolder
    </T> */
    private fun <T : RecyclerView.ViewHolder?> assertCreatedViewItem(viewHolderType: Class<T>, viewType: Int) {
        val viewHolder: RecyclerView.ViewHolder = searchResultAdapter!!.onCreateViewHolder(viewGroup!!, viewType)
        Assert.assertEquals(viewHolder.javaClass, viewHolderType)
    }
}