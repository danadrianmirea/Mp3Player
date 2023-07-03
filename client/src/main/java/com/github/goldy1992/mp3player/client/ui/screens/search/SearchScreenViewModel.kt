package com.github.goldy1992.mp3player.client.ui.screens.search

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.github.goldy1992.mp3player.client.SearchResult
import com.github.goldy1992.mp3player.client.data.repositories.media.MediaEntityUtils.createAlbum
import com.github.goldy1992.mp3player.client.data.repositories.media.MediaEntityUtils.createFolder
import com.github.goldy1992.mp3player.client.data.repositories.media.MediaEntityUtils.createSong
import com.github.goldy1992.mp3player.client.data.Playlist
import com.github.goldy1992.mp3player.client.data.SearchResults
import com.github.goldy1992.mp3player.client.data.Song
import com.github.goldy1992.mp3player.client.data.repositories.media.MediaRepository
import com.github.goldy1992.mp3player.client.ui.states.State
import com.github.goldy1992.mp3player.client.ui.viewmodel.actions.Pause
import com.github.goldy1992.mp3player.client.ui.viewmodel.actions.Play
import com.github.goldy1992.mp3player.client.ui.viewmodel.actions.SkipToNext
import com.github.goldy1992.mp3player.client.ui.viewmodel.actions.SkipToPrevious
import com.github.goldy1992.mp3player.client.ui.viewmodel.state.CurrentSongViewModelState
import com.github.goldy1992.mp3player.client.ui.viewmodel.state.IsPlayingViewModelState
import com.github.goldy1992.mp3player.commons.Constants
import com.github.goldy1992.mp3player.commons.LogTagger
import com.github.goldy1992.mp3player.commons.MediaItemBuilder
import com.github.goldy1992.mp3player.commons.MediaItemType
import com.github.goldy1992.mp3player.commons.MediaItemUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils.isEmpty
import org.apache.commons.lang3.StringUtils.isNotEmpty
import javax.inject.Inject

/**
 * [ViewModel] implementation from the [SearchScreen].
 */
@HiltViewModel
class SearchScreenViewModel
    @Inject
    constructor(
        override val mediaRepository: MediaRepository
    )

    : Pause, Play, SkipToNext, SkipToPrevious, ViewModel(), LogTagger {

    override val scope = viewModelScope
    private val _searchQuery = MutableStateFlow("")
    val searchQuery : StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            mediaRepository.currentSearchQuery()
                .collect {
                    _searchQuery.value = it
                }
        }
    }

    fun setSearchQuery(query: String) {
        viewModelScope.launch {
            if (isEmpty(query)) {
                _searchResults.value = SearchResults.NO_RESULTS
            }
            mediaRepository.search(query, Bundle())
            Log.i(logTag(), "New searchQueryValue: $query")
        }
    }

    private val _searchResults : MutableStateFlow<SearchResults> = MutableStateFlow(SearchResults(State.NOT_LOADED))
    val searchResults : StateFlow<SearchResults> = _searchResults
    init {
        viewModelScope.launch {
            mediaRepository
            .onSearchResultsChanged()
            .collect {
                if (isNotEmpty(searchQuery.value) && it.itemCount > 0) {
                    val results = mediaRepository.getSearchResults(it.query, 0, it.itemCount)
                    _searchResults.value = mapResults(results)
                    Log.i(logTag(), "got search results $results")
                } else {
                    _searchResults.value = SearchResults(State.NO_RESULTS)
                    Log.i(logTag(), "No search results returned")
                }

            }
        }
    }

    val isPlaying = IsPlayingViewModelState(mediaRepository, viewModelScope)
    val currentSong = CurrentSongViewModelState(mediaRepository, viewModelScope)

    fun play(song: Song) {
        viewModelScope.launch {
            mediaRepository.play(MediaItemBuilder(song.id).build())
        }
    }

    fun playFromList(itemIndex : Int, mediaItemList : Playlist) {
        val extras = Bundle()
        extras.putString(Constants.PLAYLIST_ID, "SearchResults")

        val mediaMetadata = MediaMetadata.Builder()
            .setAlbumTitle("Search Results")
            .setExtras(extras)
            .build()
        viewModelScope.launch { mediaRepository.playFromPlaylist(itemIndex = itemIndex, items = mediaItemList.songs.map { MediaItemBuilder(it.id).build() }, playlistMetadata = mediaMetadata) }
    }

    private fun mapResults(mediaItemList: List<MediaItem>) : SearchResults {
        val resultsMap = mutableListOf<SearchResult>()

        mediaItemList.forEach {
            val result : SearchResult =
                when (MediaItemUtils.getMediaItemType(it)) {
                    MediaItemType.SONG -> SearchResult(MediaItemType.SONG, createSong(it))
                    MediaItemType.FOLDER -> SearchResult(MediaItemType.FOLDER, createFolder(it))
                    MediaItemType.ALBUM -> SearchResult(MediaItemType.ALBUM, createAlbum(it))
                    else -> SearchResult(MediaItemUtils.getMediaItemType(it), Any())
            }
            resultsMap.add(result)
        }

        return SearchResults(
            state = State.LOADED,
            resultsMap = resultsMap
        )
    }


    override fun logTag(): String {
        return "SearchScreenViewModel"
    }
}