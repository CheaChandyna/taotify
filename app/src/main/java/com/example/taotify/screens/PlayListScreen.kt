package com.example.taotify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.taotify.R
import com.example.taotify.components.playlist.TrackListing
import com.example.taotify.data.viewmodel.PlaylistsViewModel
import com.example.taotify.network.MediaRetrieval
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Secondary04

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
        Box(
          modifier = Modifier
            .background(color = Neutral01)
        ) {
          AsyncImage(
            model = coverArtURL,
            contentDescription = "playlist cover",
            placeholder = painterResource(R.drawable.ic_broken_image),
            error = painterResource(R.drawable.ic_broken_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxSize()
          )

          IconButton(
            onClick = { onBack() },
            modifier = Modifier
              .align(Alignment.TopStart)
          ) {
            Icon(
              painter = painterResource(R.drawable.back),
              contentDescription = "Back",
              tint = Secondary04,
            )
          }

          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(16.dp)
              .background(Neutral02.copy(alpha = 0.5f))
          ) {
            Text(
              text = playlist?.name ?: "",
              color = Secondary04,
              fontSize = 70.sp,
              fontFamily = CircularStd,
              fontWeight = FontWeight.Medium,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(8.dp, 4.dp)
            )
          }
        }
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
//            navController.navigate())
          }
        )
      }
    }
  }
}