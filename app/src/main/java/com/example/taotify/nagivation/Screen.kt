package com.example.taotify.nagivation

import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
  object Login : Screen("login")
  object Home : Screen("home")
  object Search : Screen("search")
  object Library : Screen("library")
}
