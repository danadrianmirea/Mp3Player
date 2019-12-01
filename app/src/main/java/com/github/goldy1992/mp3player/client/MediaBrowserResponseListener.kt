package com.github.goldy1992.mp3player.client

import android.support.v4.media.MediaBrowserCompat
import java.util.*

interface MediaBrowserResponseListener {
    fun onChildrenLoaded(parentId: String,
                         children: ArrayList<MediaBrowserCompat.MediaItem?>)
}