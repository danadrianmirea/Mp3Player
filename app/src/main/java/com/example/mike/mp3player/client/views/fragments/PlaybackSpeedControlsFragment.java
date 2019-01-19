package com.example.mike.mp3player.client.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mike.mp3player.R;
import com.example.mike.mp3player.client.views.MediaPlayerActionListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import static com.example.mike.mp3player.commons.Constants.DECREASE_PLAYBACK_SPEED;
import static com.example.mike.mp3player.commons.Constants.INCREASE_PLAYBACK_SPEED;

public class PlaybackSpeedControlsFragment extends Fragment {

    private TextView playbackSpeed;
    private AppCompatImageButton increasePlaybackSpeedButton;
    private AppCompatImageButton decreasePlaybackSpeedButton;
    private MediaPlayerActionListener mediaPlayerActionListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_playback_speed_controls, container, true);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.decreasePlaybackSpeedButton = view.findViewById(R.id.decreasePlaybackSpeed);
        this.decreasePlaybackSpeedButton.setOnClickListener((View v) -> decreasePlaybackSpeed());
        this.increasePlaybackSpeedButton = view.findViewById(R.id.increasePlaybackSpeed);
        this.increasePlaybackSpeedButton.setOnClickListener((View v) -> increasePlaybackSpeed());
        this.playbackSpeed = view.findViewById(R.id.playbackSpeedValue);
    }

    public void setPlaybackState(PlaybackStateCompat state) {
        float speed = state.getPlaybackSpeed();
        if (speed > 0) {
            updatePlaybackSpeedText(speed);
        }
    }

    private void updatePlaybackSpeedText(float speed) {
        Runnable r = () ->  playbackSpeed.setText(getString(R.string.PLAYBACK_SPEED_VALUE, speed));
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(r);
    }

    public void increasePlaybackSpeed() {
        Bundle extras = new Bundle();
        mediaPlayerActionListener.sendCustomAction(INCREASE_PLAYBACK_SPEED, extras);
    }

    public void decreasePlaybackSpeed() {
        Bundle extras = new Bundle();
        mediaPlayerActionListener.sendCustomAction(DECREASE_PLAYBACK_SPEED, extras);
    }

    public void setMediaPlayerActionListener(MediaPlayerActionListener mediaPlayerActionListener) {
        this.mediaPlayerActionListener = mediaPlayerActionListener;
    }
}
