package com.github.goldy1992.mp3player.client.views.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageButton
import com.github.goldy1992.mp3player.R
import com.github.goldy1992.mp3player.client.MediaControllerAdapter
import com.github.goldy1992.mp3player.client.activities.MediaActivityCompat
import com.github.goldy1992.mp3player.client.callbacks.playback.PlaybackStateListener
import com.github.goldy1992.mp3player.commons.Constants
import javax.inject.Inject

class PlaybackSpeedControlsFragment : AsyncFragment(), PlaybackStateListener {
    private var playbackSpeed: TextView? = null
    private var increasePlaybackSpeedButton: AppCompatImageButton? = null
    private var decreasePlaybackSpeedButton: AppCompatImageButton? = null
    private var mediaControllerAdapter: MediaControllerAdapter? = null
    private var speed = 1.0f
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initialiseDependencies()
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_playback_speed_controls, container, true)
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        decreasePlaybackSpeedButton = view.findViewById(R.id.decreasePlaybackSpeed)
        decreasePlaybackSpeedButton.setOnClickListener(View.OnClickListener { v: View? -> decreasePlaybackSpeed() })
        increasePlaybackSpeedButton = view.findViewById(R.id.increasePlaybackSpeed)
        increasePlaybackSpeedButton.setOnClickListener(View.OnClickListener { v: View? -> increasePlaybackSpeed() })
        playbackSpeed = view.findViewById(R.id.playbackSpeedValue)
        // register listeners
        mediaControllerAdapter!!.registerPlaybackStateListener(this)
        //update GUI
        onPlaybackStateChanged(mediaControllerAdapter!!.playbackStateCompat)
    }

    private fun updatePlaybackSpeedText(speed: Float) {
        val r = Runnable { playbackSpeed!!.text = getString(R.string.PLAYBACK_SPEED_VALUE, speed) }
        mainUpdater.post(r)
    }

    @VisibleForTesting
    fun increasePlaybackSpeed() {
        worker!!.post {
            val extras = Bundle()
            mediaControllerAdapter!!.sendCustomAction(Constants.INCREASE_PLAYBACK_SPEED, extras)
        }
    }

    @VisibleForTesting
    fun decreasePlaybackSpeed() {
        worker!!.post {
            val extras = Bundle()
            mediaControllerAdapter!!.sendCustomAction(Constants.DECREASE_PLAYBACK_SPEED, extras)
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
        speed = state.playbackSpeed
        if (speed > 0) {
            updatePlaybackSpeedText(speed)
        }
    }

    fun initialiseDependencies() {
        val component = (activity as MediaActivityCompat?)!!.mediaActivityCompatComponent
        component!!.inject(this)
    }

    @Inject
    fun setMediaControllerAdapter(mediaControllerAdapter: MediaControllerAdapter?) {
        this.mediaControllerAdapter = mediaControllerAdapter
    }
}