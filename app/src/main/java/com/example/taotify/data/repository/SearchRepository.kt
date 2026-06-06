package com.example.taotify.data.repository

import com.example.taotify.data.model.SearchResult
import com.example.taotify.session.SessionProvider
import com.example.taotify.utility.FetchResult
import javax.inject.Inject

class SearchRepository @Inject constructor() {
    suspend fun search(query: String): FetchResult<SearchResult> {
        val session = SessionProvider.session ?: return FetchResult.InvalidSession
        if (session.server.isBlank() || session.username.isBlank() ||
            session.salt.isBlank() || session.token.isBlank()
        ) {
            return FetchResult.InvalidSession
        }
        return try {
            val api = ApiClient.create(session.server)
            val response = api.search3(
                salt = session.salt,
                apiVersion = "1.16.1",
                username = session.username,
                token = session.token,
                query = query
            )
            val result = response.response.searchResult3
            FetchResult.Success(
                SearchResult(
                    artists = result?.artist ?: emptyList(),
                    albums = result?.album ?: emptyList(),
                    songs = result?.song ?: emptyList()
                )
            )
        } catch (e: Exception) {
            FetchResult.UnknownError(e.message)
        }
    }
}
