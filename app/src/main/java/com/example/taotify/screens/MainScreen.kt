package com.example.taotify.screens

import com.example.taotify.components.player.MediaPlayingScreen
import com.example.taotify.components.player.MiniBarMediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.taotify.components.NavigationBar
import com.example.taotify.data.viewmodel.AudioViewModel
import com.example.taotify.nagivation.Screen
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  navController: NavController,
  audioViewModel: AudioViewModel = hiltViewModel(),
  content: @Composable (PaddingValues) -> Unit
) {
  val audioState by audioViewModel.state.collectAsStateWithLifecycle()
  val playerState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var hidePlayer by remember { mutableStateOf(true) }
  val scope = rememberCoroutineScope()

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val isLoginScreen = navBackStackEntry?.destination?.route != Screen.Login.route

  Scaffold(
    bottomBar = {
      if (isLoginScreen) {
        Column {
          AnimatedVisibility(
            visible = audioState.currentSong != null,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
          ) {
            MiniBarMediaPlayer(
              state = audioState,
              onExpand = { hidePlayer = false },
            )
          }
          NavigationBar(navController)
        }
      }
    }
  ) { innerPadding ->
    Box(modifier = Modifier) {
      content(innerPadding)
    }

    if (!hidePlayer) {
      ModalBottomSheet(
        onDismissRequest = { hidePlayer = true },
        sheetState = playerState,
        dragHandle = null
      ) {
        MediaPlayingScreen(
          viewModel = audioViewModel,
          onCollapse = {
            scope.launch {
              playerState.hide()
              hidePlayer = true
            }
          }
        )
      }
    }
  }
}
