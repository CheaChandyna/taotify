package com.example.taotify.data.repository

import com.example.taotify.data.model.Playlist
import com.example.taotify.data.model.SearchAlbum
import com.example.taotify.data.model.SearchArtist
import com.example.taotify.data.model.Song
import com.example.taotify.session.SessionProvider
import com.example.taotify.utility.FetchResult
import javax.inject.Inject

class LibraryRepository @Inject constructor() {

    suspend fun getPlaylists(): FetchResult<List<Playlist>> {
        val session = SessionProvider.session ?: return FetchResult.InvalidSession
        if (session.server.isBlank() || session.username.isBlank() ||
            session.salt.isBlank() || session.token.isBlank()
        ) return FetchResult.InvalidSession
        return try {
            val api = ApiClient.create(session.server)
            val response = api.getPlaylists(
                salt = session.salt,
                apiVersion = "1.16.1",
                username = session.username,
                token = session.token
            )
            FetchResult.Success(response.`subsonic-response`.playlists?.playlist ?: emptyList())
        } catch (e: Exception) {
            FetchResult.UnknownError(e.message)
        }
    }

    suspend fun getAlbums(): FetchResult<List<SearchAlbum>> {
        val session = SessionProvider.session ?: return FetchResult.InvalidSession
        if (session.server.isBlank() || session.username.isBlank() ||
            session.salt.isBlank() || session.token.isBlank()
        ) return FetchResult.InvalidSession
        return try {
            val api = ApiClient.create(session.server)
            val response = api.getAlbumList2(
                salt = session.salt,
                apiVersion = "1.16.1",
                username = session.username,
                token = session.token
            )
            FetchResult.Success(response.response.albumList2?.album ?: emptyList())
        } catch (e: Exception) {
            FetchResult.UnknownError(e.message)
        }
    }

    suspend fun getArtists(): FetchResult<List<SearchArtist>> {
        val session = SessionProvider.session ?: return FetchResult.InvalidSession
        if (session.server.isBlank() || session.username.isBlank() ||
            session.salt.isBlank() || session.token.isBlank()
        ) return FetchResult.InvalidSession
        return try {
            val api = ApiClient.create(session.server)
            val response = api.getArtists(
                salt = session.salt,
                apiVersion = "1.16.1",
                username = session.username,
                token = session.token
            )
            val artists = response.response.artists?.index?.flatMap { it.artist } ?: emptyList()
            FetchResult.Success(artists)
        } catch (e: Exception) {
            FetchResult.UnknownError(e.message)
        }
    }

    suspend fun getStarredSongs(): FetchResult<List<Song>> {
        val session = SessionProvider.session ?: return FetchResult.InvalidSession
        if (session.server.isBlank() || session.username.isBlank() ||
            session.salt.isBlank() || session.token.isBlank()
        ) return FetchResult.InvalidSession
        return try {
            val api = ApiClient.create(session.server)
            val response = api.getStarred2(
                salt = session.salt,
                apiVersion = "1.16.1",
                username = session.username,
                token = session.token
            )
            FetchResult.Success(response.response.starred2?.song ?: emptyList())
        } catch (e: Exception) {
            FetchResult.UnknownError(e.message)
        }
    }
}
