package com.example.taotify.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.example.taotify.data.model.Song
import com.example.taotify.data.repository.BrowsingRepository
import com.example.taotify.network.MediaRetrieval.getStreamingUrl
import com.example.taotify.utility.FetchResult
import com.example.taotify.utility.MediaPlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
  private val mediaPlayerManager: MediaPlayerManager, private val browsingRepository: BrowsingRepository
) : ViewModel() {
  val state = mediaPlayerManager.state
  var error by mutableStateOf<String?>(null)
  var song by mutableStateOf<Song?>(null)

  //remove this if implement manaul progress bar
  val player: Player get() = mediaPlayerManager.player

  private suspend fun resolveSong(id: String): Song? {
    val songResult = browsingRepository.getSong(id)

    return if (songResult is FetchResult.Success) {
      songResult.data.copy(url = getStreamingUrl(id))
    } else null
  }

  fun playSingle(songId: String) {
    viewModelScope.launch {
      val song = resolveSong(songId) ?: return@launch
      mediaPlayerManager.play(song)
    }
  }

  fun playQueue(songIds: List<String>, startIndex: Int? = null) {
    viewModelScope.launch {
      val songs = songIds.map { id -> async { resolveSong(id) } }.awaitAll().filterNotNull()

      if (songs.isNotEmpty()) {
        mediaPlayerManager.playQueue(songs, startIndex)
      }
    }
  }

  fun togglePause() {
    mediaPlayerManager.togglePause()
  }

  fun stop() {
    mediaPlayerManager.stop()
  }

  fun seekTo(friction: Float) {
    mediaPlayerManager.seekTo(friction)
  }

  fun skipToNext() {
    mediaPlayerManager.skipToNextTrack()
  }

  fun backToPrevious() {
    mediaPlayerManager.backToPreviousTrack()
  }

  fun toggleShuffle() {
    mediaPlayerManager.toggleShuffle()
  }

  fun toggleRepeat() {
    mediaPlayerManager.toggleRepeat()
  }

  override fun onCleared() {
    mediaPlayerManager.stop()
  }
}
