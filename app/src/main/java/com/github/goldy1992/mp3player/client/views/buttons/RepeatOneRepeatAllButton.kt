package com.github.goldy1992.mp3player.client.views.buttons

import android.content.Context
import android.os.Handler
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import com.github.goldy1992.mp3player.R
import com.github.goldy1992.mp3player.client.MediaControllerAdapter
import com.github.goldy1992.mp3player.client.callbacks.playback.PlaybackStateListener
import com.github.goldy1992.mp3player.commons.Constants
import javax.inject.Inject
import javax.inject.Named

/**
 *
 */
class RepeatOneRepeatAllButton @Inject constructor(context: Context, mediaControllerAdapter: MediaControllerAdapter,
                                                   @Named("main") mainUpdater: Handler) : MediaButton(context, mediaControllerAdapter, mainUpdater), PlaybackStateListener {
    @PlaybackStateCompat.RepeatMode
    var state = 0
        private set

    override fun init(view: ImageView?) {
        super.init(view)
        mediaControllerAdapter.registerPlaybackStateListener(this)
        updateState(mediaControllerAdapter.repeatMode)
    }

    @VisibleForTesting
    override fun onClick(view: View?) {
        val nextState = nextState
        mediaControllerAdapter.repeatMode = nextState
    }

    fun updateState(@PlaybackStateCompat.RepeatMode newState: Int) {
        when (newState) {
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                mainUpdater.post { setRepeatAllIcon() }
                state = PlaybackStateCompat.REPEAT_MODE_ALL
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                mainUpdater.post { setRepeatOneIcon() }
                state = PlaybackStateCompat.REPEAT_MODE_ONE
            }
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                mainUpdater.post { setRepeatNoneIcon() }
                state = PlaybackStateCompat.REPEAT_MODE_NONE
            }
            else -> {
                val sb = StringBuilder().append("Invalid RepeatMode param: ").append(newState)
                Log.e(LOG_TAG, sb.toString())
            }
        }
    }

    private fun setRepeatAllIcon() {
        setImage(R.drawable.ic_baseline_repeat_24px, Constants.OPAQUE)
    }

    private fun setRepeatOneIcon() {
        setImage(R.drawable.ic_baseline_repeat_one_24px, Constants.OPAQUE)
    }

    private fun setRepeatNoneIcon() {
        setImage(R.drawable.ic_baseline_repeat_24px, Constants.TRANSLUCENT)
    }

    @VisibleForTesting
    fun setRepeatMode(@PlaybackStateCompat.RepeatMode repeatMode: Int) {
        state = repeatMode
    }

    private val nextState: Int
        private get() = when (state) {
            PlaybackStateCompat.REPEAT_MODE_ALL -> PlaybackStateCompat.REPEAT_MODE_NONE
            PlaybackStateCompat.REPEAT_MODE_NONE -> PlaybackStateCompat.REPEAT_MODE_ONE
            PlaybackStateCompat.REPEAT_MODE_ONE -> PlaybackStateCompat.REPEAT_MODE_ALL
            else -> PlaybackStateCompat.REPEAT_MODE_NONE
        }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
        val newRepeatMode = mediaControllerAdapter.repeatMode
        if (this.state != newRepeatMode) {
            updateState(newRepeatMode)
        }
    }

    companion object {
        private const val LOG_TAG = "RPT1_RPT_ALL_BTN"
    }
}