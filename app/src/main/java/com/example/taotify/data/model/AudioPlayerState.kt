package com.example.taotify.data.model

data class AudioPlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean= false,
    val isLoading: Boolean = false,
    val duration: Long = 0L,
    val position: Long = 0L,
    val error: String? = null,
    val isReady: Boolean = false,
    val isShuffled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
) {
    val progress: Float
        get() = if (duration > 0) position.toFloat() / duration.toFloat() else 0f
}

enum class RepeatMode { NONE, ONE, ALL }