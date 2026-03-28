package com.example.taotify.utility

sealed class FetchResult<out T> {
  data class Success<T>(val data: T) : FetchResult<T>()
  data class UnknownError(val message: String?) : FetchResult<Nothing>()
  object InvalidSession : FetchResult<Nothing>()
}