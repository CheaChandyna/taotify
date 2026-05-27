package com.example.taotify.data.repository

import com.example.taotify.data.model.Song
import com.example.taotify.session.SessionProvider
import com.example.taotify.utility.FetchResult
import javax.inject.Inject

class BrowsingRepository @Inject constructor() {
    suspend fun getSong(songId: String): FetchResult<Song> {
        val session = SessionProvider.session ?: return FetchResult.InvalidSession
        val server = session.server
        val username = session.username
        val salt = session.salt
        val token = session.token

        if (
            server.isNullOrBlank() ||
            username.isNullOrBlank() ||
            salt.isNullOrBlank() ||
            token.isNullOrBlank()
        ) {
            return FetchResult.InvalidSession
        }

        return try {
            val api = ApiClient.create(server)

            val response = api.getSong(
                salt = salt,
                apiVersion = "1.16.1",
                username = username,
                token = token,
                id = songId
            )

            FetchResult.Success(response.`subsonic-response`.song!!)

        } catch (e: Exception) {
            return FetchResult.UnknownError(e.message)
        }
    }
}