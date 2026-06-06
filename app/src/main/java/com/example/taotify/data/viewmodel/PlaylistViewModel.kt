package com.example.taotify.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taotify.data.model.Playlist
import com.example.taotify.data.model.Song
import com.example.taotify.data.repository.PlaylistRepository
import com.example.taotify.utility.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
  private val repository: PlaylistRepository
) : ViewModel() {

  var loading by mutableStateOf(true)
  var error by mutableStateOf<String?>(null)
  var playlists by mutableStateOf<List<Playlist>>(emptyList())
  var playlistEntries by mutableStateOf<List<Song>>(emptyList())

  init {
    refresh()
  }

  fun getPlaylistById(playlistId: String): Playlist? =
    playlists.find { it.id == playlistId }

  fun refresh() {
    viewModelScope.launch {
      loading = true
      when (val result = repository.getPlaylists()) {
        is FetchResult.Success -> playlists = result.data
        is FetchResult.InvalidSession -> error = "Session is invalid or has expired. Please log in again."
        is FetchResult.UnknownError -> error = result.message ?: "An unknown error occurred."
      }
      loading = false
    }
  }

  fun fetchPlaylist(playlistId: String) {
    viewModelScope.launch {
      loading = true
      when (val result = repository.getPlaylist(playlistId)) {
        is FetchResult.Success -> playlistEntries = result.data
        is FetchResult.InvalidSession -> error = "Session is invalid or has expired. Please log in again."
        is FetchResult.UnknownError -> error = result.message ?: "An unknown error occurred."
      }
      loading = false
    }
  }

  fun createPlaylist(name: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      when (val result = repository.createPlaylist(name)) {
        is FetchResult.Success -> {
          playlists = playlists + result.data
          onSuccess()
        }
        is FetchResult.InvalidSession -> error = "Session is invalid or has expired."
        is FetchResult.UnknownError -> error = result.message ?: "Failed to create playlist."
      }
    }
  }

  fun addSongToPlaylist(playlistId: String, songId: String, onSuccess: () -> Unit = {}) {
    viewModelScope.launch {
      when (val result = repository.addSongToPlaylist(playlistId, songId)) {
        is FetchResult.Success -> onSuccess()
        is FetchResult.InvalidSession -> error = "Session is invalid or has expired."
        is FetchResult.UnknownError -> error = result.message ?: "Failed to add song."
      }
    }
  }

  fun removeSongFromPlaylist(playlistId: String, songIndex: Int) {
    viewModelScope.launch {
      // Optimistic local removal
      playlistEntries = playlistEntries.toMutableList().also { it.removeAt(songIndex) }
      when (val result = repository.removeSongFromPlaylist(playlistId, songIndex)) {
        is FetchResult.UnknownError -> {
          error = result.message ?: "Failed to remove song."
          fetchPlaylist(playlistId) // revert by reloading
        }
        else -> {}
      }
    }
  }
}
