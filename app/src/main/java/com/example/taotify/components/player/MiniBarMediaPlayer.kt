package com.example.taotify.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.taotify.R
import com.example.taotify.data.model.AudioPlayerState
import com.example.taotify.data.viewmodel.AudioViewModel
import com.example.taotify.network.MediaRetrieval
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Secondary02

@Composable
fun MiniBarMediaPlayer(
  modifier: Modifier = Modifier,
  state: AudioPlayerState,
  onExpand: () -> Unit,
  audioViewModel: AudioViewModel = hiltViewModel()
) {

  val currentSong = state.currentSong
  val coverArtURL = remember(currentSong?.coverArt) {
    if (currentSong != null) MediaRetrieval.getCoverArt(currentSong.coverArt) else null
  }

  val iconContainer = 36.dp
  val iconSize = 28.dp

  AnimatedVisibility(
    visible = currentSong != null,
    enter   = slideInVertically { it },
    exit    = slideOutVertically { it },
  ) {
    Surface(
      tonalElevation = 8.dp,
      color = Secondary02.copy(alpha = 0.6f),
      shape = RoundedCornerShape(6.dp),
      modifier = modifier
        .padding(horizontal = 8.dp)
        .clickable{ onExpand() }
    ) {
      Box(
        modifier = Modifier
          .height(59.dp)
          .fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
              .fillMaxHeight()
              .weight(1f)
          ) {
            Box(
              modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(color = Neutral01)
            ) {
              AsyncImage(
                model = coverArtURL,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_broken_image),
                error = painterResource(R.drawable.ic_broken_image),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxHeight()
              )
            }

            Column(
              modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            ) {
              Text(
                text = currentSong?.title ?: "",
                fontSize = 18.sp,
                color = Neutral01,
                fontFamily = CircularStd,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )

              currentSong?.artist?.let {
                Text(
                  text = it,
                  color = Neutral02,
                  fontSize = 14.sp,
                  fontFamily = CircularStd,
                  fontWeight = FontWeight.Medium,
                )
              }
            }
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              modifier = Modifier
                .size(iconContainer)
                .rotate(180f),
              onClick = { audioViewModel.backToPrevious() }
            ) {
              Icon(
                painter = painterResource(R.drawable.back_treck),
                contentDescription = "back",
                tint = Neutral01,
                modifier = Modifier.size(iconSize)
              )
            }

            IconButton(
              modifier = Modifier.size(40.dp),
              onClick = { audioViewModel.togglePause() }
            ) {
              Icon(
                painter = if (state.isPlaying) painterResource(R.drawable.pause) else painterResource(R.drawable.play),
                contentDescription = if (state.isPlaying) "play" else "pause",
                tint = Neutral01,
                modifier = Modifier.size(32.dp)
              )
            }

            IconButton(
              modifier = Modifier.size(iconContainer),
              onClick = { audioViewModel.skipToNext() },
            ) {
              Icon(
                painter = painterResource(R.drawable.back_treck),
                contentDescription = "next",
                tint = Neutral01,
                modifier = Modifier.size(iconSize)
              )
            }
          }
        }
      }
    }
  }
}
