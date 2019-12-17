package com.github.goldy1992.mp3player.client.views.viewholders

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import com.github.goldy1992.mp3player.client.AlbumArtPainter
import com.github.goldy1992.mp3player.client.R
import com.github.goldy1992.mp3player.client.utils.TimerUtils.formatTime
import com.github.goldy1992.mp3player.commons.Constants
import com.github.goldy1992.mp3player.commons.MediaItemUtils
import com.github.goldy1992.mp3player.commons.MetaDataKeys
import org.apache.commons.io.FilenameUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_album_art.view.*
import kotlinx.android.synthetic.main.song_item_menu.view.*

class MySongViewHolder(itemView: View, albumArtPainter: AlbumArtPainter?)
    : MediaItemViewHolder(itemView, albumArtPainter), LayoutContainer {
    override val containerView: View?
        get() = itemView

    override fun bindMediaItem(item: MediaBrowserCompat.MediaItem) { // - get element from your dataset at this position
// - replace the contents of the views with that element
        val title : String? = extractTitle(item)
        val artist : String? = extractArtist(item)
        val duration : String? = extractDuration(item)
        itemView.artist.text = artist
        itemView.title.text = title
        itemView.duration.text = duration
        val uri : Uri? = MediaItemUtils.getAlbumArtUri(item)
        if (null != uri) {
            albumArtPainter!!.paintOnView(itemView.albumArt, uri)
        }
    }

    private fun extractTitle(song: MediaBrowserCompat.MediaItem): String {
        val charSequence: CharSequence? = MediaItemUtils.getTitle(song)
        if (null == charSequence) {
            val fileName = if (MediaItemUtils.hasExtras(song)) MediaItemUtils.getExtra(MetaDataKeys.META_DATA_KEY_FILE_NAME, song) as? String else null
            if (fileName != null) {
                return FilenameUtils.removeExtension(fileName)
            }
        } else {
            return charSequence.toString()
        }
        return Constants.UNKNOWN
    }

    private fun extractDuration(song: MediaBrowserCompat.MediaItem): String? {
        val extras = song.description.extras
        if (null != extras) {
            val duration = extras.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            return formatTime(duration)
        }
        return null
    }

    private fun extractArtist(song: MediaBrowserCompat.MediaItem): String? {
        var artist: String? = null
        try {
            artist = MediaItemUtils.getArtist(song)
            if (null == artist) {
                artist = Constants.UNKNOWN
            }
        } catch (ex: NullPointerException) {
            artist = Constants.UNKNOWN
        }
        return artist
    }


}