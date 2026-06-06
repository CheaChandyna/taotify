package com.example.taotify.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral01
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Neutral03
import com.example.taotify.ui.theme.Primary01
import com.example.taotify.ui.theme.Secondary02
import com.example.taotify.ui.theme.Secondary04

@Composable
fun AuthTextField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  placeHolder: String,
  isPassword: Boolean = false
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      fontFamily = CircularStd,
      fontWeight = FontWeight.SemiBold,
      fontSize = 13.sp,
      color = Neutral01,
      modifier = Modifier.padding(bottom = 6.dp)
    )

    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      placeholder = {
        Text(
          placeHolder,
          fontFamily = CircularStd,
          fontWeight = FontWeight.Normal,
          color = Neutral02,
          fontSize = 15.sp
        )
      },
      visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
      shape = RoundedCornerShape(8.dp),
      colors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Secondary02,
        focusedContainerColor = Secondary02,
        focusedTextColor = Secondary04,
        unfocusedTextColor = Secondary04,
        cursorColor = Primary01,
        unfocusedBorderColor = Neutral03,
        focusedBorderColor = Primary01,
      ),
      singleLine = true,
      modifier = Modifier.fillMaxWidth()
    )
  }
}
