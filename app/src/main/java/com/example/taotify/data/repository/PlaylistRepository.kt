package com.example.taotify.data.repository

import com.example.taotify.data.model.Playlist
import com.example.taotify.data.model.Song
import com.example.taotify.session.SessionProvider
import com.example.taotify.utility.FetchResult
import javax.inject.Inject

class PlaylistRepository @Inject constructor() {

  private fun session() = SessionProvider.session

  private fun isSessionValid(session: com.example.taotify.data.UserSession?) =
    session != null &&
      session.server.isNotBlank() &&
      session.username.isNotBlank() &&
      session.salt.isNotBlank() &&
      session.token.isNotBlank()

  suspend fun getPlaylists(): FetchResult<List<Playlist>> {
    val session = session() ?: return FetchResult.InvalidSession
    if (!isSessionValid(session)) return FetchResult.InvalidSession
    return try {
      val api = ApiClient.create(session.server)
      val response = api.getPlaylists(
        salt = session.salt, apiVersion = "1.16.1",
        username = session.username, token = session.token
      )
      FetchResult.Success(response.`subsonic-response`.playlists?.playlist ?: emptyList())
    } catch (e: Exception) {
      FetchResult.UnknownError(e.message)
    }
  }

  suspend fun getPlaylist(playlistId: String): FetchResult<List<Song>> {
    val session = session() ?: return FetchResult.InvalidSession
    if (!isSessionValid(session)) return FetchResult.InvalidSession
    return try {
      val api = ApiClient.create(session.server)
      val response = api.getPlaylist(
        salt = session.salt, apiVersion = "1.16.1",
        username = session.username, token = session.token,
        id = playlistId
      )
      FetchResult.Success(response.`subsonic-response`.playlist?.entry ?: emptyList())
    } catch (e: Exception) {
      FetchResult.UnknownError(e.message)
    }
  }

  suspend fun createPlaylist(name: String): FetchResult<Playlist> {
    val session = session() ?: return FetchResult.InvalidSession
    if (!isSessionValid(session)) return FetchResult.InvalidSession
    return try {
      val api = ApiClient.create(session.server)
      val response = api.createPlaylist(
        salt = session.salt, apiVersion = "1.16.1",
        username = session.username, token = session.token,
        name = name
      )
      val playlist = response.`subsonic-response`.playlist
        ?: return FetchResult.UnknownError("No playlist returned")
      FetchResult.Success(playlist)
    } catch (e: Exception) {
      FetchResult.UnknownError(e.message)
    }
  }

  suspend fun addSongToPlaylist(playlistId: String, songId: String): FetchResult<Unit> {
    val session = session() ?: return FetchResult.InvalidSession
    if (!isSessionValid(session)) return FetchResult.InvalidSession
    return try {
      val api = ApiClient.create(session.server)
      api.updatePlaylist(
        salt = session.salt, apiVersion = "1.16.1",
        username = session.username, token = session.token,
        playlistId = playlistId, songIdToAdd = songId
      )
      FetchResult.Success(Unit)
    } catch (e: Exception) {
      FetchResult.UnknownError(e.message)
    }
  }

  suspend fun removeSongFromPlaylist(playlistId: String, songIndex: Int): FetchResult<Unit> {
    val session = session() ?: return FetchResult.InvalidSession
    if (!isSessionValid(session)) return FetchResult.InvalidSession
    return try {
      val api = ApiClient.create(session.server)
      api.updatePlaylist(
        salt = session.salt, apiVersion = "1.16.1",
        username = session.username, token = session.token,
        playlistId = playlistId, songIndexToRemove = songIndex
      )
      FetchResult.Success(Unit)
    } catch (e: Exception) {
      FetchResult.UnknownError(e.message)
    }
  }
}
