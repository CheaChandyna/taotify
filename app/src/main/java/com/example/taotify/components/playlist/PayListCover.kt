package com.example.taotify.components.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.taotify.R
import com.example.taotify.data.model.Playlist
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Secondary04

@Composable()
fun PlayListCover(
  coverArtURL: String?,
  onBack: () -> Unit,
  playlist: Playlist?
) {
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