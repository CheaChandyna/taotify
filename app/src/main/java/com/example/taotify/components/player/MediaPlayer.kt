package com.example.taotify.components.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taotify.R
import com.example.taotify.data.model.RepeatMode
import com.example.taotify.data.viewmodel.AudioViewModel
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Primary01
import com.example.taotify.ui.theme.Secondary04

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaPlayer(
  audioViewModel: AudioViewModel = hiltViewModel()
) {
  val state by audioViewModel.state.collectAsStateWithLifecycle()
  val iconSize = 24.dp
  val iconContainer = 32.dp

  Column(
    modifier = Modifier.padding(horizontal = 16.dp)
  ) {
    LinearWavyProgressIndicator(
      progress = { state.progress },
      modifier = Modifier.fillMaxWidth()
    )

    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text =  state.position.formatMs(),
        color = Secondary04
      )
      Text(
        text = state.duration.formatMs(),
        color = Secondary04
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        modifier = Modifier.size(iconContainer),
        onClick = { audioViewModel.toggleShuffle() },
      ) {
        Icon(
          painter = painterResource(R.drawable.shuffle),
          contentDescription = "shuffle",
          tint = if(state.isShuffled) Primary01 else Neutral01,
          modifier = Modifier.size(iconSize)
        )
      }

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
        modifier = Modifier.size(80.dp),
        onClick = { audioViewModel.togglePause() },
      ) {
        Icon(
          painter =if (state.isPlaying) painterResource(R.drawable.pause) else painterResource(R.drawable.play),
          contentDescription = if (state.isPlaying) "play" else "pause",
          tint = Neutral01,
          modifier = Modifier.size(72.dp)
        )
      }

      IconButton(
        modifier = Modifier.size(iconContainer),
        onClick = { audioViewModel.skipToNext() },
      ) {
        Icon(
          painter = painterResource(R.drawable.back_treck),
          contentDescription = "back",
          tint = Neutral01,
          modifier = Modifier.size(iconSize)
        )
      }

      IconButton(
        modifier = Modifier.size(iconContainer),
        onClick = { audioViewModel.toggleRepeat() },
      ) {
        Icon(
          painter = painterResource(
            when (state.repeatMode) {
              RepeatMode.NONE -> R.drawable.repeat        // grey
              RepeatMode.ALL  -> R.drawable.repeat        // colored
              RepeatMode.ONE  -> R.drawable.repeat_one    // colored
            },
          ),

          contentDescription = "repeat",
          tint = when (state.repeatMode) {
            RepeatMode.NONE -> Neutral01
            else            -> Primary01
          },
          modifier = Modifier.size(iconSize)
        )
      }
    }
  }
}

private fun Long.formatMs(): String {
  val s = this / 1000
  return "%d:%02d".format(s / 60, s % 60)
}