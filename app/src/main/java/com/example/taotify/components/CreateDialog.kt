import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CreateDialog(onDismissRequest: () -> Unit) {
  Dialog(
    onDismissRequest = { onDismissRequest() },
    properties = DialogProperties(
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(0.dp, 0.dp, 0.dp, 80.dp),
      contentAlignment = Alignment.BottomCenter,
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(250.dp),
      ) {
        Text(
          "Bottom Dialog",
          modifier = Modifier.padding(16.dp)
        )
      }
    }
  }
}
