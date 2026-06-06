package com.example.taotify.data.model

data class Song (
    val url: String? = null,
    val id: String,
    val title: String,
    val coverArt: String?,
    val artist: String?,
    val album: String,
    val duration: Int,
    val playCount: Int,
)