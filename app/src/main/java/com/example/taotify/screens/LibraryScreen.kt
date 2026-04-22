package com.example.taotify.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taotify.components.PageHeader

@Composable
fun LibraryScreen(
  modifier: Modifier = Modifier
) {
  Column(modifier.padding(16.dp)) {
    PageHeader("Your Library")
  }
}