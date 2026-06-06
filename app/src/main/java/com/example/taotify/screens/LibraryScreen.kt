package com.example.taotify.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.taotify.R
import com.example.taotify.components.PageHeader
import com.example.taotify.components.playlist.TrackListing
import com.example.taotify.data.model.Playlist
import com.example.taotify.data.model.SearchAlbum
import com.example.taotify.data.model.SearchArtist
import com.example.taotify.data.viewmodel.AudioViewModel
import com.example.taotify.data.viewmodel.LibraryViewModel
import com.example.taotify.nagivation.Screen
import com.example.taotify.network.MediaRetrieval
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Neutral03
import com.example.taotify.ui.theme.Primary01
import com.example.taotify.ui.theme.Secondary01
import com.example.taotify.ui.theme.Secondary02
import com.example.taotify.ui.theme.Secondary04

@Composable
fun LibraryScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    audioViewModel: AudioViewModel = hiltViewModel(),
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val tabs = listOf("Playlists", "Albums", "Artists", "Songs")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        PageHeader("Your Library")
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, label ->
                PillChip(
                    label = label,
                    selected = viewModel.selectedTab == index,
                    onClick = { viewModel.selectedTab = index }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (viewModel.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Primary01,
                trackColor = Secondary02
            )
            Spacer(Modifier.height(8.dp))
        }

        viewModel.error?.let { err ->
            Text(
                text = err,
                color = Color.Red,
                fontFamily = CircularStd,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            when (viewModel.selectedTab) {
                0 -> {
                    if (viewModel.playlists.isEmpty() && !viewModel.isLoading) {
                        item { LibraryEmptyMessage("No playlists yet") }
                    } else {
                        items(viewModel.playlists, key = { it.id }) { playlist ->
                            LibraryPlaylistRow(playlist) {
                                navController.navigate(Screen.Playlist.createRoute(playlist.id))
                            }
                        }
                    }
                }
                1 -> {
                    if (viewModel.albums.isEmpty() && !viewModel.isLoading) {
                        item { LibraryEmptyMessage("No albums found") }
                    } else {
                        items(viewModel.albums, key = { it.id }) { album ->
                            LibraryAlbumRow(album)
                        }
                    }
                }
                2 -> {
                    if (viewModel.artists.isEmpty() && !viewModel.isLoading) {
                        item { LibraryEmptyMessage("No artists found") }
                    } else {
                        items(viewModel.artists, key = { it.id }) { artist ->
                            LibraryArtistRow(artist)
                        }
                    }
                }
                3 -> {
                    if (viewModel.songs.isEmpty() && !viewModel.isLoading) {
                        item { LibraryEmptyMessage("No liked songs yet.\nStar songs to see them here.") }
                    } else {
                        itemsIndexed(viewModel.songs, key = { _, song -> song.id }) { index, song ->
                            TrackListing(
                                index = index + 1,
                                entry = song,
                                onItemClick = { audioViewModel.playSingle(it.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PillChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Primary01 else Secondary02)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Secondary01 else Neutral01,
            fontFamily = CircularStd,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LibraryPlaylistRow(playlist: Playlist, onClick: () -> Unit) {
    val coverArtUrl = remember(playlist.coverArt) { MediaRetrieval.getCoverArt(playlist.coverArt) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
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
                text = playlist.name,
                color = Secondary04,
                fontFamily = CircularStd,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${playlist.songCount} songs",
                color = Neutral02,
                fontFamily = CircularStd,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun LibraryAlbumRow(album: SearchAlbum) {
    val coverArtUrl = remember(album.coverArt) { MediaRetrieval.getCoverArt(album.coverArt) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .padding(vertical = 8.dp),
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
private fun LibraryArtistRow(artist: SearchArtist) {
    val coverArtUrl = remember(artist.coverArt) { MediaRetrieval.getCoverArt(artist.coverArt) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(vertical = 8.dp),
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
private fun LibraryEmptyMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Neutral02,
            fontFamily = CircularStd,
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
