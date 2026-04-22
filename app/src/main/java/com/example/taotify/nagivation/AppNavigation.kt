package com.example.taotify.nagivation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.taotify.components.NavigationBar
import com.example.taotify.data.UserPreferences
import com.example.taotify.screens.HomeScreen
import com.example.taotify.screens.LibraryScreen
import com.example.taotify.screens.LoginScreen
import com.example.taotify.screens.MediaPlayingScreen
import com.example.taotify.screens.PlaylistScreen
import com.example.taotify.screens.SearchScreen
import com.example.taotify.session.SessionProvider
import kotlinx.coroutines.flow.first

@Composable
fun AppNavigation(
  context: Context
) {

  val navController = rememberNavController()
  var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
      val session = UserPreferences.session(context).first()
      if (session.token.isNotEmpty()) {
        SessionProvider.session = session
        startDestination = Screen.Main.route
      } else
        startDestination = Screen.Login.route
    }

  if (startDestination == null) return

  NavHost(
    navController = navController,
    startDestination = startDestination!!
  ) {
    composable(Screen.Login.route) {
      LoginScreen(
        onLoginSuccess = {
          navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Login.route) { inclusive = true }
          }
        }
      )
    }

    navigation(startDestination = Screen.Home.route, route = Screen.Main.route) {
      composable(Screen.Home.route) {
        MainScreen(navController) { HomeScreen(navController) }
      }
      composable(Screen.Search.route) {
        MainScreen(navController) { SearchScreen() }
      }
      composable(Screen.Library.route) {
        MainScreen(navController) { LibraryScreen() }
      }

      composable(Screen.Playlist.route) { backStackEntry ->
        val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
        PlaylistScreen(
          navController = navController,
          playlistId = playlistId,
          onBack = { navController.popBackStack() }
        )
      }

      composable(Screen.MediaPlayingScreen.route) {
        backStackEntry -> val songId = backStackEntry.arguments?.getString("songId") ?: ""
        MediaPlayingScreen()
      }
    }
  }
}

@Composable
fun MainScreen(
  navController: NavController,
  content: @Composable (PaddingValues) -> Unit
) {
  Scaffold(
//    modifier = Modifier.fillMaxSize(),
    bottomBar = { NavigationBar(navController) }
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      content(innerPadding)
    }
  }
}
