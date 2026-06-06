package com.example.taotify.data.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taotify.data.model.SearchAlbum
import com.example.taotify.data.model.SearchArtist
import com.example.taotify.data.model.Song
import com.example.taotify.data.repository.SearchRepository
import com.example.taotify.utility.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {
    var query by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var artists by mutableStateOf<List<SearchArtist>>(emptyList())
    var albums by mutableStateOf<List<SearchAlbum>>(emptyList())
    var songs by mutableStateOf<List<Song>>(emptyList())
    var hasSearched by mutableStateOf(false)

    fun search() {
        if (query.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            error = null
            when (val result = repository.search(query)) {
                is FetchResult.Success -> {
                    artists = result.data.artists
                    albums = result.data.albums
                    songs = result.data.songs
                    hasSearched = true
                }
                is FetchResult.InvalidSession -> {
                    error = "Session expired. Please log in again."
                }
                is FetchResult.UnknownError -> {
                    error = result.message ?: "An unknown error occurred."
                }
            }
            isLoading = false
        }
    }

    fun clearResults() {
        artists = emptyList()
        albums = emptyList()
        songs = emptyList()
        error = null
        hasSearched = false
    }
}
