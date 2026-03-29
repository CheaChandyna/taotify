package com.example.taotify.components.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.taotify.R
import com.example.taotify.data.model.Playlist
import com.example.taotify.network.MediaRetrieval
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Secondary02
import com.example.taotify.ui.theme.Secondary04

@Composable
fun PlayListListing(
  playlist: Playlist,
  onItemClick: (Playlist) -> Unit
) {

  //Hold the state of cover art
  val coverArtURL = remember(playlist.coverArt) {
    MediaRetrieval.getCoverArt(playlist.coverArt)
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(4.dp))
      .background(Secondary02)
      .clickable{ onItemClick(playlist) }
  ) {

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier
        .width(218.dp)
        .height(66.dp)
        .padding(8.dp, 0.dp)
    ) {
      AsyncImage(
        model = coverArtURL,
        contentDescription = null,
        placeholder = painterResource(R.drawable.ic_broken_image),
        error = painterResource(R.drawable.ic_broken_image),
        alignment = Alignment.Center,
        modifier = Modifier
          .size(50.dp)
          .background(Secondary02)
          .clip(RoundedCornerShape(25.dp))
      )

      Text(
        text = playlist.name,
        fontSize = 12.sp,
        fontFamily = CircularStd,
        color = Secondary04
      )
    }
  }
}