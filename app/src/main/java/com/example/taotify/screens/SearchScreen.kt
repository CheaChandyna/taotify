package com.example.taotify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.taotify.R
import com.example.taotify.components.PageHeader
import com.example.taotify.components.playlist.TrackListing
import com.example.taotify.data.model.SearchAlbum
import com.example.taotify.data.model.SearchArtist
import com.example.taotify.data.viewmodel.AudioViewModel
import com.example.taotify.data.viewmodel.SearchViewModel
import com.example.taotify.network.MediaRetrieval
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Neutral03
import com.example.taotify.ui.theme.Primary01
import com.example.taotify.ui.theme.Secondary01
import com.example.taotify.ui.theme.Secondary02
import com.example.taotify.ui.theme.Secondary04
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    audioViewModel: AudioViewModel = hiltViewModel(),
    viewModel: SearchViewModel = hiltViewModel()
) {
    val tabs = listOf("Artists", "Albums", "Songs")
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(viewModel.query) {
        if (viewModel.query.isBlank()) {
            viewModel.clearResults()
            return@LaunchedEffect
        }
        delay(500)
        viewModel.search()
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                PageHeader("Search")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = viewModel.query,
                    onValueChange = { viewModel.query = it },
                    placeholder = {
                        Text(
                            "Artists, albums, or songs...",
                            color = Neutral02,
                            fontFamily = CircularStd
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (viewModel.query.isNotEmpty()) {
                            IconButton(onClick = {
                                viewModel.query = ""
                                viewModel.clearResults()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Secondary02,
                        focusedContainerColor = Secondary02,
                        focusedTextColor = Secondary04,
                        unfocusedTextColor = Secondary04,
                        cursorColor = Primary01,
                        unfocusedBorderColor = Neutral03,
                        focusedBorderColor = Primary01,
                        unfocusedLeadingIconColor = Neutral02,
                        focusedLeadingIconColor = Primary01,
                        unfocusedTrailingIconColor = Neutral02,
                        focusedTrailingIconColor = Neutral01
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                        viewModel.search()
                    })
                )
                if (viewModel.isLoading) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Primary01,
                        trackColor = Secondary02
                    )
                }
                viewModel.error?.let { err ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = err,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = CircularStd
                    )
                }
            }
        }

        if (!viewModel.isLoading && !viewModel.hasSearched && viewModel.query.isBlank()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Search for your favorite\nartists, albums, or songs",
                        color = Neutral02,
                        fontFamily = CircularStd,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (viewModel.hasSearched && !viewModel.isLoading) {
            stickyHeader {
                val counts = listOf(
                    viewModel.artists.size,
                    viewModel.albums.size,
                    viewModel.songs.size
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Secondary01,
                    contentColor = Primary01
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = "$title (${counts[index]})",
                                    fontFamily = CircularStd,
                                    color = if (selectedTab == index) Primary01 else Neutral02,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    if (viewModel.artists.isEmpty()) {
                        item { EmptyResultMessage("No artists found") }
                    } else {
                        items(viewModel.artists, key = { it.id }) { artist ->
                            ArtistRow(artist)
                        }
                    }
                }
                1 -> {
                    if (viewModel.albums.isEmpty()) {
                        item { EmptyResultMessage("No albums found") }
                    } else {
                        items(viewModel.albums, key = { it.id }) { album ->
                            AlbumRow(album)
                        }
                    }
                }
                2 -> {
                    if (viewModel.songs.isEmpty()) {
                        item { EmptyResultMessage("No songs found") }
                    } else {
                        itemsIndexed(viewModel.songs, key = { _, song -> song.id }) { index, song ->
                            TrackListing(
                                index = index + 1,
                                entry = song,
                                onItemClick = { audioViewModel.playSingle(it.id) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistRow(artist: SearchArtist) {
    val coverArtUrl = remember(artist.coverArt) { MediaRetrieval.getCoverArt(artist.coverArt) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Neutral03)
        ) {
            AsyncImage(
                model = coverArtUrl,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_broken_image),
                error = painterResource(R.drawable.ic_broken_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = artist.name,
                color = Secondary04,
                fontFamily = CircularStd,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (artist.albumCount > 0) {
                Text(
                    text = "${artist.albumCount} albums",
                    color = Neutral02,
                    fontFamily = CircularStd,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun AlbumRow(album: SearchAlbum) {
    val coverArtUrl = remember(album.coverArt) { MediaRetrieval.getCoverArt(album.coverArt) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(Neutral03)
        ) {
            AsyncImage(
                model = coverArtUrl,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_broken_image),
                error = painterResource(R.drawable.ic_broken_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = album.name,
                color = Secondary04,
                fontFamily = CircularStd,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    album.artist?.let { append(it) }
                    album.year?.let { year ->
                        if (album.artist != null) append(" • ")
                        append(year)
                    }
                },
                color = Neutral02,
                fontFamily = CircularStd,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyResultMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Neutral02,
            fontFamily = CircularStd,
            fontSize = 16.sp
        )
    }
}
