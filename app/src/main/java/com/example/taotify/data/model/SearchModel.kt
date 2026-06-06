package com.example.taotify.data.model

data class SearchArtist(
    val id: String,
    val name: String,
    val coverArt: String? = null,
    val albumCount: Int = 0
)

data class SearchAlbum(
    val id: String,
    val name: String,
    val artist: String? = null,
    val artistId: String? = null,
    val year: Int? = null,
    val coverArt: String? = null,
    val songCount: Int = 0
)

data class SearchResult3(
    val artist: List<SearchArtist> = emptyList(),
    val album: List<SearchAlbum> = emptyList(),
    val song: List<Song> = emptyList()
)

data class SearchResult(
    val artists: List<SearchArtist>,
    val albums: List<SearchAlbum>,
    val songs: List<Song>
)
