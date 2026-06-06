package com.example.taotify.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.taotify.R
import com.example.taotify.data.model.Playlist
import com.example.taotify.data.model.Song
import com.example.taotify.network.MediaRetrieval
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Neutral03
import com.example.taotify.ui.theme.Primary01
import com.example.taotify.ui.theme.Secondary02
import com.example.taotify.ui.theme.Secondary04

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongActionSheet(
    song: Song,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onAddToPlaylist: (Playlist) -> Unit,
    onRemoveFromPlaylist: (() -> Unit)? = null
) {
    var showPlaylistPicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val coverArtUrl = remember(song.coverArt) { MediaRetrieval.getCoverArt(song.coverArt) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Secondary02,
        dragHandle = null
    ) {
        if (showPlaylistPicker) {
            // — Playlist picker view —
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showPlaylistPicker = false }) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back",
                        tint = Neutral01
                    )
                }
                Text(
                    text = "Add to playlist",
                    color = Secondary04,
                    fontFamily = CircularStd,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            HorizontalDivider(color = Neutral03.copy(alpha = 0.3f))

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No playlists yet. Create one first.",
                        color = Neutral02,
                        fontFamily = CircularStd
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
                    items(playlists, key = { it.id }) { playlist ->
                        PlaylistPickerRow(
                            playlist = playlist,
                            onClick = {
                                onAddToPlaylist(playlist)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        } else {
            // — Song actions view —
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
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
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        color = Secondary04,
                        fontFamily = CircularStd,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist ?: song.album,
                        color = Neutral02,
                        fontFamily = CircularStd,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = Neutral03.copy(alpha = 0.3f))

            SheetAction(
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Neutral01) },
                label = "Add to playlist",
                onClick = { showPlaylistPicker = true }
            )

            if (onRemoveFromPlaylist != null) {
                SheetAction(
                    icon = { Icon(Icons.Default.Close, contentDescription = null, tint = Primary01) },
                    label = "Remove from playlist",
                    labelColor = Primary01,
                    onClick = {
                        onRemoveFromPlaylist()
                        onDismiss()
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SheetAction(
    icon: @Composable () -> Unit,
    label: String,
    labelColor: androidx.compose.ui.graphics.Color = Secondary04,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon()
        Text(
            text = label,
            color = labelColor,
            fontFamily = CircularStd,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun PlaylistPickerRow(playlist: Playlist, onClick: () -> Unit) {
    val coverArtUrl = remember(playlist.coverArt) { MediaRetrieval.getCoverArt(playlist.coverArt) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
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
                modifier = Modifier.fillMaxHeight()
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                color = Secondary04,
                fontFamily = CircularStd,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${playlist.songCount} songs",
                color = Neutral02,
                fontFamily = CircularStd,
                fontSize = 13.sp
            )
        }
        Icon(
            painter = painterResource(R.drawable.plus),
            contentDescription = null,
            tint = Neutral02,
            modifier = Modifier.size(20.dp)
        )
    }
}
