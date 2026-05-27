package com.example.taotify.utility

import android.content.Context
import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.PlaybackException
import com.example.taotify.data.model.AudioPlayerState
import com.example.taotify.data.model.RepeatMode
import com.example.taotify.data.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.getValue

@Singleton
class MediaPlayerManager @Inject constructor (
  @ApplicationContext private val context: Context
) {
  private val _player: ExoPlayer by lazy {
    ExoPlayer.Builder(context).build().also { it.addListener(playerListener) }
  }
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private val _state = MutableStateFlow(AudioPlayerState())
  val state: StateFlow<AudioPlayerState> = _state.asStateFlow()
  private var progressJob: Job? = null
  private var _queue: List<Song> = emptyList()
  private var _originalQueue: List<Song> = emptyList()
  private var _queueIndex: Int = -1
  private var _repeatMode: RepeatMode = RepeatMode.NONE

  companion object {
    private const val PROGRESS_INTERVAL_MS = 200L
  }

  //remove this if implement manaul progress bar
  val player: Player get() = _player

  // Players type
  fun play(song: Song?) {
    if (song == null) return
    if (state.value.currentSong?.id == song.id && _state.value.isReady) {
      togglePause()
      return
    }

    _state.value = _state.value.copy(
      currentSong = song,
      isLoading = true,
      position = 0L,
      error = null
    )
    player.setMediaItem(
      MediaItem.Builder()
        .setUri(song.url)
        .setMediaId(song.id)
        .build()
    )
    player.prepare()
    player.playWhenReady = true
  }

  fun playQueue(songs: List<Song>, startIndex: Int? = null) {
    _originalQueue = songs

    if (_state.value.isShuffled) {
      if (startIndex != null) {
        val remain = songs.filter { it.id != songs[startIndex].id }.shuffled()
        _queue = listOf(songs[startIndex]) + remain
      } else {
        _queue = songs.shuffled()
      }
      _queueIndex = 0
    } else {
      _queue = songs
      _queueIndex = startIndex ?: 0
    }

    play(_queue[_queueIndex])
  }

  // Controls
  fun toggleShuffle() {
    val newShuffledState = !_state.value.isShuffled
    _state.value = _state.value.copy(isShuffled = newShuffledState)

    if (_state.value.currentSong != null) {
      val currentSong = _state.value.currentSong!!
      if (newShuffledState) {
        val played = _queue.take(_queueIndex)
        val remain = _queue
          .drop(_queueIndex + 1)
          .shuffled()

        _queue = played + listOf(currentSong) + remain
        _queueIndex = played.size
      } else {
        _queue = _originalQueue
        _queueIndex = _originalQueue
          .indexOfFirst { it.id == currentSong.id }
          .coerceAtLeast(0)

      }
    }
  }

  fun toggleRepeat() {
    _repeatMode = when (_repeatMode) {
      RepeatMode.NONE -> RepeatMode.ONE
      RepeatMode.ONE -> RepeatMode.ALL
      RepeatMode.ALL -> RepeatMode.NONE
    }

    _state.value = _state.value.copy(repeatMode = _repeatMode)
  }

  fun togglePause() {
    if (player.isPlaying) player.pause() else player.play()
  }

  fun skipToNextTrack() {
    val next = _queueIndex + 1
    if(next < _queue.size) {
      _queueIndex = next
      play(_queue[_queueIndex])
    }
  }

  fun backToPreviousTrack() {
    val previous = _queueIndex - 1
    if(previous >= 0) {
      _queueIndex = previous
      play(_queue[_queueIndex])
    }
  }

  fun stop() {
    player.stop()
    stopProgressPolling()
    _queue         = emptyList()
    _originalQueue = emptyList()
    _queueIndex    = -1
    _state.value   = AudioPlayerState(isShuffled = _state.value.isShuffled)
  }

  fun seekTo(friction: Float) {
    val target = (friction * player.duration).toLong().coerceAtLeast(0)
    player.seekTo(target)
    _state.value = _state.value.copy(position = target)
  }

  fun seekToMs(positionMs: Long) {
    player.seekTo(positionMs)
    _state.value = _state.value.copy(position = positionMs)
  }

  fun release() {
    scope.cancel()
    player.removeListener(playerListener)
    player.release()
  }

  fun appendToQueue(songs: List<Song>) {
    _queue = _queue + songs
  }

  fun updateQueue(songs: List<Song>, index: Int) {
    _queue = songs
    _queueIndex = index
  }

  // Internal
  private val playerListener = object : Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
      _state.value = _state.value.copy(
        isLoading = playbackState == Player.STATE_BUFFERING,
        isReady = playbackState == Player.STATE_READY,
        duration = player.duration.coerceAtLeast(0)
      )

      if (playbackState == Player.STATE_ENDED) {
        stopProgressPolling()
        handleTrackEnd()
      }
    }
    override fun onIsPlayingChanged(isPlaying: Boolean) {
      _state.value = _state.value.copy(isPlaying = isPlaying)
      if (isPlaying)
        startProgressPolling()
      else
        stopProgressPolling()
    }

    override fun onPlayerError(error: PlaybackException) {
      _state.value = _state.value.copy(error = error.message, isPlaying = false, isLoading = false)
    }
  }

  private fun handleTrackEnd() {
    when(_repeatMode) {
      RepeatMode.ONE -> {
        player.seekTo(0)
        player.playWhenReady = true
      }
      RepeatMode.ALL -> {
        if (_queueIndex + 1 < _queue.size) {
          skipToNextTrack()
        } else {
          _queueIndex = 0
          play(_queue[_queueIndex])
        }
      }
      RepeatMode.NONE -> {
        if (_queueIndex + 1 < _queue.size) {
          skipToNextTrack()
        } else {
          _state.value = _state.value.copy(isPlaying = false, position = 0L)
        }
      }
    }
  }

  private fun startProgressPolling() {
    progressJob?.cancel()
    progressJob = scope.launch {
      while (isActive) {
        _state.value = _state.value.copy(
          position = player.currentPosition.coerceAtLeast(0),
          duration = player.duration.coerceAtLeast(0)
        )
        delay(PROGRESS_INTERVAL_MS)
      }
    }
  }

  private fun stopProgressPolling() {
    progressJob?.cancel()
    progressJob = null
  }
}
