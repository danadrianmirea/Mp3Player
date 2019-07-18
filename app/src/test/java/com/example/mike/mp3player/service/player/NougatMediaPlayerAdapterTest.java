package com.example.mike.mp3player.service.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.mike.mp3player.service.AudioFocusManager;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class NougatMediaPlayerAdapterTest extends MediaPlayerAdapterTestBase {

    /** {@inheritDoc} */
    @Before
    public void setup() throws IllegalAccessException {
        super.setup();
    }

    @Override
    NougatMediaPlayerAdapter createMediaPlayerAdapter() {
        NougatMediaPlayerAdapter nougatPlayerAdapter = new NougatMediaPlayerAdapter(context, audioFocusManager);
        nougatPlayerAdapter.setOnCompletionListener(mockOnCompletionListener);
        nougatPlayerAdapter.setOnSeekCompleteListener(mockOnSeekCompleteListener);
        return nougatPlayerAdapter;
    }
}