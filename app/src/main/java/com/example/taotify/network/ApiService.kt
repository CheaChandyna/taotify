package com.example.taotify.network

import com.example.taotify.data.model.Playlist
import com.example.taotify.data.model.Playlists
import com.example.taotify.data.model.SearchAlbum
import com.example.taotify.data.model.SearchArtist
import com.example.taotify.data.model.SearchResult3
import com.example.taotify.data.model.Song
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class PingWrapper(
  val `subsonic-response`: PingResponse
)

data class PingResponse(
  val status: String,
  val version: String,
  val type: String,
  val serverVersion: String,
  val openSubsonic: Boolean
)

data class SubsonicResponse<T>(
  @SerializedName("subsonic-response")
  val `subsonic-response`: SubsonicMeta<T>
)

data class SubsonicMeta<T>(
  val status: String,
  val version: String,
  val type: String,
  val serverVersion: String,
  val openSubsonic: Boolean,
  val playlists: T? = null,
  val playlist: T? = null,
  val song: T? = null
)

data class SearchWrapper(
  @SerializedName("subsonic-response")
  val response: SearchMeta
)

data class SearchMeta(
  val status: String,
  val searchResult3: SearchResult3? = null
)

data class AlbumListWrapper(
  @SerializedName("subsonic-response")
  val response: AlbumListMeta
)

data class AlbumListMeta(
  val status: String,
  val albumList2: AlbumListContent? = null
)

data class AlbumListContent(
  val album: List<SearchAlbum> = emptyList()
)

data class ArtistsWrapper(
  @SerializedName("subsonic-response")
  val response: ArtistsMeta
)

data class ArtistsMeta(
  val status: String,
  val artists: ArtistsContent? = null
)

data class ArtistsContent(
  val index: List<ArtistIndexGroup> = emptyList()
)

data class ArtistIndexGroup(
  val name: String,
  val artist: List<SearchArtist> = emptyList()
)

data class Starred2Wrapper(
  @SerializedName("subsonic-response")
  val response: Starred2Meta
)

data class Starred2Meta(
  val status: String,
  val starred2: SearchResult3? = null
)

interface ApiService {
  @GET("rest/ping.view")
  suspend fun ping(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String
  ): PingWrapper

  @GET("rest/getPlaylists")
  suspend fun getPlaylists(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String
  ): SubsonicResponse<Playlists>

  @GET("rest/getPlaylist")
  suspend fun getPlaylist(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String,
    @Query("id") id: String
  ): SubsonicResponse<Playlist>

  @GET("rest/getSong")
  suspend fun getSong(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String,
    @Query("id") id: String
  ): SubsonicResponse<Song>

  @GET("rest/search3")
  suspend fun search3(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String,
    @Query("query") query: String,
    @Query("artistCount") artistCount: Int = 20,
    @Query("albumCount") albumCount: Int = 20,
    @Query("songCount") songCount: Int = 20
  ): SearchWrapper

  @GET("rest/getAlbumList2")
  suspend fun getAlbumList2(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String,
    @Query("type") type: String = "alphabeticalByName",
    @Query("size") size: Int = 100
  ): AlbumListWrapper

  @GET("rest/getArtists")
  suspend fun getArtists(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String
  ): ArtistsWrapper

  @GET("rest/getStarred2")
  suspend fun getStarred2(
    @Query("s") salt: String,
    @Query("v") apiVersion: String,
    @Query("c") client: String = "taotify",
    @Query("u") username: String,
    @Query("f") format: String = "json",
    @Query("t") token: String
  ): Starred2Wrapper
}
