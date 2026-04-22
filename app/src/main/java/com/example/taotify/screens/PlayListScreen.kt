package com.example.taotify.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.taotify.R
import com.example.taotify.components.playlist.PlayListCover
import com.example.taotify.components.playlist.TrackListing
import com.example.taotify.data.viewmodel.PlaylistsViewModel
import com.example.taotify.nagivation.Screen
import com.example.taotify.network.MediaRetrieval
import com.example.taotify.ui.theme.Neutral01
@Composable
fun PlaylistScreen(
  navController: NavController,
  playlistId: String,
  onBack: () -> Unit,
  viewModel: PlaylistsViewModel = hiltViewModel()
) {

  val playlist = viewModel.getPlaylistById(playlistId)
  val coverArtURL = remember(playlist?.coverArt) {
    MediaRetrieval.getCoverArt(playlist?.coverArt ?: "")
  }

  LaunchedEffect(playlistId) {
    viewModel.fetchPlaylist(playlistId)
  }

  Scaffold { paddingValues ->
    LazyColumn(
      contentPadding = paddingValues
    ) {
      item {
        PlayListCover(coverArtURL, onBack, playlist);
      }

      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically

        ) {
          Box(
            modifier = Modifier.clickable {},
            contentAlignment = Alignment.Center
          ) {
            Icon(
              painter = painterResource(R.drawable.playlist_pause),
              contentDescription = null,
              tint = Color.Unspecified
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(30.dp)
          ) {
            Box(
              modifier = Modifier.clickable { },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                painter = painterResource(R.drawable.shuffle),
                contentDescription = null,
                tint = Neutral01,
              )
            }

            Box(
              modifier = Modifier.clickable { },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                painter = painterResource(R.drawable.more),
                contentDescription = null,
                tint = Neutral01,
              )
            }
          }
        }
      }

      itemsIndexed(viewModel.playlistEntries) { index, entry ->
        TrackListing(
          index = index + 1,
          entry =  entry,
          modifier = Modifier.padding(horizontal = 16.dp),
          onItemClick = { entry ->
            navController.navigate(Screen.MediaPlayingScreen.createRoute(entry.id))
          }
        )
      }
    }
  }
}