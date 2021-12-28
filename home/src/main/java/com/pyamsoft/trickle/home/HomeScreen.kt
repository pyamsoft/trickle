package com.pyamsoft.trickle.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding

@Composable
@JvmOverloads
fun HomeScreen(
    modifier: Modifier = Modifier,
    @StringRes appNameRes: Int,
    onCopy: (String) -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenApplicationSettings: () -> Unit,
) {
  val scaffoldState = rememberScaffoldState()

  Scaffold(
      modifier = modifier,
      scaffoldState = scaffoldState,
  ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).systemBarsPadding(),
    ) {
      item {
        Header(
            modifier = Modifier.fillMaxWidth(),
            appNameRes = appNameRes,
            onOpenApplicationSettings = onOpenApplicationSettings,
        )
      }

      item {
        AdbInstructions(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onCopy = onCopy,
        )
      }

      item {
        GoToSettings(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onOpenSettings = onOpenBatterySettings,
        )
      }
    }
  }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    @StringRes appNameRes: Int,
    onOpenApplicationSettings: () -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.Top,
  ) {
    Text(
        modifier = Modifier.weight(1F),
        textAlign = TextAlign.Center,
        text = stringResource(appNameRes),
        style = MaterialTheme.typography.h3,
    )

    IconButton(
        modifier = Modifier.padding(start = 16.dp),
        onClick = onOpenApplicationSettings,
    ) {
      Icon(
          imageVector = Icons.Filled.Settings,
          contentDescription = "Open Settings",
      )
    }
  }
}

@Composable
private fun AdbInstructions(
    modifier: Modifier = Modifier,
    onCopy: (String) -> Unit,
) {
  val context = LocalContext.current
  val command =
      remember(context) {
        "adb shell pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS"
      }

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
        text = "You must grant permission via (all one line):",
        style = MaterialTheme.typography.body1,
    )

    SelectionContainer {
      Text(
          modifier =
              Modifier.clickable { onCopy(command) }
                  .padding(top = 16.dp)
                  .background(color = Color.Black)
                  .padding(16.dp),
          text = command,
          style =
              MaterialTheme.typography.body2.copy(
                  color = Color.Green,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
              ),
      )
    }

    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = { onCopy(command) },
    ) {
      Text(
          text = "Copy to Clipboard",
      )
    }
  }
}

@Composable
private fun GoToSettings(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit,
) {
  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text =
            "If it does not seem to be working, go to your system settings and toggle the power setting until you confirm it takes effect from the system Settings, then try this app again.",
        style = MaterialTheme.typography.body1,
    )

    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = onOpenSettings,
    ) {
      Text(
          text = "Open System Settings",
      )
    }
  }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
  HomeScreen(
      appNameRes = 0,
      onOpenBatterySettings = {},
      onOpenApplicationSettings = {},
      onCopy = {},
  )
}
