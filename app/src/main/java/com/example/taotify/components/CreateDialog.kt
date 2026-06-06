package com.example.taotify.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taotify.data.viewmodel.PlaylistsViewModel
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Neutral03
import com.example.taotify.ui.theme.Primary01
import com.example.taotify.ui.theme.Secondary02
import com.example.taotify.ui.theme.Secondary04

@Composable
fun CreateDialog(
    onDismissRequest: () -> Unit,
    viewModel: PlaylistsViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Secondary02)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "New Playlist",
                    color = Secondary04,
                    fontFamily = CircularStd,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text("Playlist name", color = Neutral02, fontFamily = CircularStd)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Secondary04,
                        unfocusedTextColor = Secondary04,
                        cursorColor = Primary01,
                        unfocusedBorderColor = Neutral03,
                        focusedBorderColor = Primary01
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel", color = Neutral02, fontFamily = CircularStd)
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.createPlaylist(name, onSuccess = onDismissRequest)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary01),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            "Create",
                            color = Secondary04,
                            fontFamily = CircularStd,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
