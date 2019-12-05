package com.github.goldy1992.mp3player.dagger.components.fragments;

import com.github.goldy1992.mp3player.client.views.fragments.MediaControlsFragment;
import com.github.goldy1992.mp3player.client.views.fragments.PlayToolbarFragment;
import com.github.goldy1992.mp3player.commons.dagger.scopes.FragmentScope;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent
public interface PlaybackButtonsSubComponent {

    void inject(MediaControlsFragment mediaControlsFragment);
    void inject(PlayToolbarFragment playToolBarFragment);
}
