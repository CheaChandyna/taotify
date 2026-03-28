package com.example.taotify.components

import CreateDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.taotify.nagivation.Screen
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Primary01
import com.example.taotify.ui.theme.Secondary01
import com.example.taotify.R

data class NavigationItem(
  val title: String,
  val icon: Int,
  val iconBold: Int,
  val route: String
)

@Composable
fun NavigationBar(
    navController: NavController
) {
  val selectedNavigationIndex = rememberSaveable {
    mutableIntStateOf(0)
  }

  val navigationItems = listOf<NavigationItem>(
    NavigationItem(
      title= "Home",
      icon= R.drawable.home,
      iconBold = R.drawable.home_bold,
      route = Screen.Home.route
    ),
    NavigationItem(
      title= "Search",
      icon= R.drawable.search,
      iconBold = R.drawable.search_bold,
      route = Screen.Search.route
    ),
    NavigationItem(
      title= "Library",
      icon= R.drawable.library,
      iconBold = R.drawable.library_bold,
      route = Screen.Library.route
    ),
    NavigationItem(
      title= "Create",
      icon= R.drawable.plus,
      iconBold = R.drawable.plus,
      route = Screen.Home.route
    ),
  )

  NavigationBar(
    containerColor = Secondary01
  ) {
    var showCreateDialog by rememberSaveable {
      mutableStateOf(false)
    }

    navigationItems.forEachIndexed { index, item ->
      NavigationBarItem(
        selected = selectedNavigationIndex.intValue == index,
        onClick = {
          if (item.title == "Create") {
            showCreateDialog = true
          } else {
            selectedNavigationIndex.intValue = index
            navController.navigate(item.route)
          }
        },
        icon = {
          var icon = item.icon
          if (selectedNavigationIndex.intValue == index) {
            icon = item.iconBold
          }
          Icon(painterResource(icon), contentDescription = item.title)
        },
        label = {
          Text(
            item.title,
            color = Neutral02,
            fontWeight = if (selectedNavigationIndex.intValue == index) FontWeight.Bold else FontWeight.Normal
          )
        },
        colors = NavigationBarItemDefaults.colors(
          indicatorColor = Secondary01,
        )
      )
    }
    if (showCreateDialog) {
      CreateDialog(
        onDismissRequest = { showCreateDialog = false }
      )
    }
  }
}
