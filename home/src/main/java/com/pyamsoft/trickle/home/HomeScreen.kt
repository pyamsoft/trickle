package com.pyamsoft.trickle.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsHeight

private val WHITESPACE_REGEX = Regex("\\s+")
private val DOWNLOAD_ADB_BLURB =
    """
    You must first download the Android Debug Bridge (ADB) and install it
    on a Laptop or Desktop
"""
        .trimIndent()
        .replace(WHITESPACE_REGEX, " ")

private val ENABLE_USB_DEBUGGING_BLURB =
    """
    Then you must enable Developer Settings on your device,
    enable USB Debugging, and connect your device to your Laptop or Desktop.
"""
        .trimIndent()
        .replace(WHITESPACE_REGEX, " ")

private val PERMISSION_REQUIRED_BLURB =
    """
    You must grant permission to change system battery settings via ADB.
    In a command-line interface, you must run the following command
"""
        .trimIndent()
        .replace(WHITESPACE_REGEX, " ")

private val RESTART_APP_BLURB =
    """
    Once you have run the ADB command, you must restart this app.
    Close and swipe away this application from your recents screen, or click
    the button below.
"""
        .trimIndent()
        .replace(WHITESPACE_REGEX, " ")

private val RESOLUTION_APP =
    """
    If it does not seem to be working, click this button a few
    times and see if that fixes the problem
    """
        .trimIndent()
        .replace(WHITESPACE_REGEX, " ")
private val RESOLUTION_SYSTEM_SERVICES =
    """
    And if that still does not work, open the System Settings
    and toggle Power-Saving mode on and off a couple times until
    it manually begins to work again, then try again.
    """
        .trimIndent()
        .replace(WHITESPACE_REGEX, " ")

@Composable
@JvmOverloads
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeViewState,
    @StringRes appNameRes: Int,
    onTogglePowerSaving: (Boolean) -> Unit,
    onCopy: (String) -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenApplicationSettings: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRestartApp: () -> Unit,
) {
  val scaffoldState = rememberScaffoldState()
  val hasPermission = state.hasPermission

  Scaffold(
      modifier = modifier,
      scaffoldState = scaffoldState,
  ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    ) {
      item {
        Spacer(
            modifier = Modifier.statusBarsHeight(additional = 16.dp),
        )
      }

      item {
        Header(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            appNameRes = appNameRes,
            onOpenApplicationSettings = onOpenApplicationSettings,
        )
      }

      item {
        Crossfade(
            modifier = Modifier.fillMaxWidth(),
            targetState = hasPermission,
        ) { hasPerm ->
          if (hasPerm) {
            PowerSavingSettings(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                onTogglePowerSaving = onTogglePowerSaving,
                onOpenBatterySettings = onOpenBatterySettings,
                onRestartPowerService = onRestartPowerService,
            )
          } else {
            SetupInstructions(
                modifier = Modifier.fillMaxWidth(),
                onCopy = onCopy,
                onRestartApp = onRestartApp,
            )
          }
        }
      }

      item {
        Spacer(
            modifier = Modifier.navigationBarsHeight(additional = 16.dp),
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
    Column(
        modifier = Modifier.weight(1F),
    ) {
      Text(
          textAlign = TextAlign.Center,
          text = stringResource(appNameRes),
          style = MaterialTheme.typography.h4,
      )

      Text(
          modifier = Modifier.padding(top = 8.dp),
          text = "Automatically enter power-saving mode",
          style = MaterialTheme.typography.body2,
      )
    }

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
private fun PowerSavingSettings(
    modifier: Modifier = Modifier,
    state: HomeViewState,
    onOpenBatterySettings: () -> Unit,
    onTogglePowerSaving: (Boolean) -> Unit,
    onRestartPowerService: () -> Unit,
) {
  val isPowerSaving = state.isPowerSaving
  Column(
      modifier = modifier,
  ) {
    ServiceSwitch(
        modifier = Modifier.fillMaxWidth(),
        isPowerSaving = isPowerSaving,
        onTogglePowerSaving = onTogglePowerSaving,
    )

    GoToSettings(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        onOpenSettings = onOpenBatterySettings,
        onRestartPowerService = onRestartPowerService,
    )
  }
}

@Composable
private fun ServiceSwitch(
    modifier: Modifier = Modifier,
    isPowerSaving: Boolean,
    onTogglePowerSaving: (Boolean) -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        modifier = Modifier.weight(1F),
        text = "Automatically enter power-saving mode when screen is off",
        style = MaterialTheme.typography.body2,
    )

    Switch(
        modifier = Modifier.padding(start = 16.dp),
        checked = isPowerSaving,
        onCheckedChange = onTogglePowerSaving,
    )
  }
}

@Composable
private fun SetupInstructions(
    modifier: Modifier = Modifier,
    onCopy: (String) -> Unit,
    onRestartApp: () -> Unit,
) {
  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        text = DOWNLOAD_ADB_BLURB,
        style = MaterialTheme.typography.body2,
    )

    Text(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        text = ENABLE_USB_DEBUGGING_BLURB,
        style = MaterialTheme.typography.body2,
    )

    AdbInstructions(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        onCopy = onCopy,
        onRestartApp = onRestartApp,
    )
  }
}

@Composable
private fun AdbInstructions(
    modifier: Modifier = Modifier,
    onCopy: (String) -> Unit,
    onRestartApp: () -> Unit,
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
        text = PERMISSION_REQUIRED_BLURB,
        style = MaterialTheme.typography.body2,
    )

    SelectionContainer {
      Text(
          modifier =
              Modifier.clickable { onCopy(command) }
                  .padding(top = 16.dp)
                  .background(color = Color.DarkGray)
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

    Text(
        modifier = Modifier.padding(top = 24.dp),
        text = RESTART_APP_BLURB,
        style = MaterialTheme.typography.body2,
    )

    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = onRestartApp,
    ) {
      Text(
          text = "Kill Application",
      )
    }
  }
}

private const val MAX_CLICKS_SHOW_EXTRA_HELP = 5

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun GoToSettings(
    modifier: Modifier = Modifier,
    onRestartPowerService: () -> Unit,
    onOpenSettings: () -> Unit,
) {
  var clicks by remember { mutableStateOf(0) }
  val isVisible = remember(clicks) { clicks > MAX_CLICKS_SHOW_EXTRA_HELP }

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = RESOLUTION_APP,
        style = MaterialTheme.typography.body1,
    )

    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = {
          clicks += 1
          onRestartPowerService()
        },
    ) {
      Text(
          text = "Restart Power Service",
      )
    }

    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        visible = isVisible,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = RESOLUTION_SYSTEM_SERVICES,
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
  }
}

@Composable
private fun PreviewHomeScreen(state: HomeViewState) {
  HomeScreen(
      state = state,
      appNameRes = 0,
      onTogglePowerSaving = {},
      onOpenBatterySettings = {},
      onOpenApplicationSettings = {},
      onCopy = {},
      onRestartPowerService = {},
      onRestartApp = {},
  )
}

@Preview
@Composable
private fun PreviewHomeScreenNoPermission() {
  PreviewHomeScreen(
      state = MutableHomeViewState().apply { hasPermission = false },
  )
}

@Preview
@Composable
private fun PreviewHomeScreenHasPermission() {
  PreviewHomeScreen(
      state = MutableHomeViewState().apply { hasPermission = true },
  )
}
