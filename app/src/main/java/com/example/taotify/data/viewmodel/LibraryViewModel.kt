package com.example.taotify.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taotify.data.model.Playlist
import com.example.taotify.data.model.SearchAlbum
import com.example.taotify.data.model.SearchArtist
import com.example.taotify.data.model.Song
import com.example.taotify.data.repository.LibraryRepository
import com.example.taotify.utility.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    var selectedTab by mutableIntStateOf(0)
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var playlists by mutableStateOf<List<Playlist>>(emptyList())
    var albums by mutableStateOf<List<SearchAlbum>>(emptyList())
    var artists by mutableStateOf<List<SearchArtist>>(emptyList())
    var songs by mutableStateOf<List<Song>>(emptyList())

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val playlistsJob = async { repository.getPlaylists() }
            val albumsJob = async { repository.getAlbums() }
            val artistsJob = async { repository.getArtists() }
            val songsJob = async { repository.getStarredSongs() }

            when (val r = playlistsJob.await()) {
                is FetchResult.Success -> playlists = r.data
                is FetchResult.InvalidSession -> error = "Session expired. Please log in again."
                is FetchResult.UnknownError -> error = r.message
            }
            when (val r = albumsJob.await()) {
                is FetchResult.Success -> albums = r.data
                else -> {}
            }
            when (val r = artistsJob.await()) {
                is FetchResult.Success -> artists = r.data
                else -> {}
            }
            when (val r = songsJob.await()) {
                is FetchResult.Success -> songs = r.data
                else -> {}
            }

            isLoading = false
        }
    }
}
