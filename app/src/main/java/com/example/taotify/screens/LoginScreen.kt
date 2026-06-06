package com.example.taotify.screens

import ApiClient.generateSalt
import ApiClient.md5
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taotify.R
import com.example.taotify.components.AuthTextField
import com.example.taotify.components.PrimaryButton
import com.example.taotify.data.UserPreferences
import com.example.taotify.data.UserSession
import com.example.taotify.session.SessionProvider
import com.example.taotify.ui.theme.CircularStd
import com.example.taotify.ui.theme.Neutral02
import com.example.taotify.ui.theme.Secondary04
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class LoginResult {
  object Success : LoginResult()
  object InvalidCredentials : LoginResult()
  object ServerUnreachable : LoginResult()
}

suspend fun login(
  context: Context,
  serverAddress: String,
  username: String,
  password: String
): LoginResult {
  val salt = generateSalt()
  val token = md5(password + salt)
  val api = ApiClient.create(baseUrl = serverAddress)

  return try {
    val response = api.ping(
      salt = salt,
      apiVersion = "1.16.1",
      username = username,
      token = token
    )

    if (response.`subsonic-response`.status == "ok") {
      val session = UserSession(serverAddress, username, token, salt)
      SessionProvider.session = session
      UserPreferences.save(context, serverAddress, username, salt, token)
      LoginResult.Success
    } else {
      LoginResult.InvalidCredentials
    }

  } catch (e: UnknownHostException) {
    LoginResult.ServerUnreachable
  } catch (e: ConnectException) {
    LoginResult.ServerUnreachable
  } catch (e: SocketTimeoutException) {
    LoginResult.ServerUnreachable
  }
}

@Composable
fun LoginScreen(
  modifier: Modifier = Modifier,
  onLoginSuccess: () -> Unit
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  var serverAddress by remember { mutableStateOf("") }
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var loading by remember { mutableStateOf(false) }
  var error by remember { mutableStateOf<String?>(null) }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 32.dp)
  ) {
    Spacer(Modifier.height(72.dp))

    Image(
      painter = painterResource(id = R.drawable.taotify),
      contentDescription = "Logo",
      modifier = Modifier.size(72.dp)
    )

    Spacer(Modifier.height(20.dp))

    Text(
      text = "Log in to Taotify",
      fontFamily = CircularStd,
      fontWeight = FontWeight.Bold,
      fontSize = 26.sp,
      color = Secondary04
    )

    Spacer(Modifier.height(8.dp))

    Text(
      text = "Connect to your Navidrome server",
      fontFamily = CircularStd,
      fontWeight = FontWeight.Normal,
      fontSize = 14.sp,
      color = Neutral02
    )

    Spacer(Modifier.height(36.dp))

    AuthTextField(
      label = "Server Address",
      value = serverAddress,
      onValueChange = { serverAddress = it; error = null },
      placeHolder = "http://ip:port",
    )

    Spacer(Modifier.height(16.dp))

    AuthTextField(
      label = "Username",
      value = username,
      onValueChange = { username = it; error = null },
      placeHolder = "Enter your username",
    )

    Spacer(Modifier.height(16.dp))

    AuthTextField(
      label = "Password",
      value = password,
      onValueChange = { password = it; error = null },
      placeHolder = "Enter your password",
      isPassword = true,
    )

    error?.let {
      Spacer(Modifier.height(12.dp))
      Text(
        text = it,
        color = Color(0xFFE74C3C),
        fontFamily = CircularStd,
        fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth()
      )
    }

    Spacer(Modifier.height(28.dp))

    PrimaryButton(
      loading = loading,
      label = "Log in",
      modifier = Modifier.fillMaxWidth(),
      onClick = {
        val server = serverAddress.trim()
        val user = username.trim()
        if (server.isEmpty() || user.isEmpty() || password.isEmpty()) {
          error = "Please fill in all fields"
          return@PrimaryButton
        }
        scope.launch {
          loading = true
          error = null
          when (login(context, server, user, password)) {
            LoginResult.Success -> onLoginSuccess()
            LoginResult.InvalidCredentials -> {
              loading = false
              error = "Wrong username or password"
            }
            LoginResult.ServerUnreachable -> {
              loading = false
              error = "Cannot reach server. Check the address and try again."
            }
          }
        }
      }
    )

    Spacer(Modifier.height(40.dp))
  }
}
