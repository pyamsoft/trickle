package com.pyamsoft.trickle.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.ui.icons.renderPYDroidExtras

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeViewState,
    appName: String,
    hasNotificationPermission: Boolean,
    onTogglePowerSaving: (Boolean) -> Unit,
    onToggleIgnoreInPowerSavingMode: (Boolean) -> Unit,
    onToggleExitWhileCharging: (Boolean) -> Unit,
    onCopy: (String) -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenApplicationSettings: () -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRestartApp: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
  val scaffoldState = rememberScaffoldState()
  val hasPermission = state.hasPermission
  val isLoading = state.loading

  val (isTroubleshooting, setTroubleShooting) = remember { mutableStateOf(false) }

  Scaffold(
      modifier = modifier,
      scaffoldState = scaffoldState,
  ) { pv ->
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.keylines.content),
    ) {
      item {
        Spacer(
            modifier =
                Modifier.padding(pv).statusBarsPadding().height(MaterialTheme.keylines.content),
        )
      }

      item {
        Header(
            modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.content),
            appName = appName,
            onOpenApplicationSettings = onOpenApplicationSettings,
        )
      }

      renderPYDroidExtras()

      if (isLoading) {
        item {
          Loading(
              modifier = Modifier.fillMaxWidth(),
          )
        }
      } else {
        if (hasPermission) {
          renderPowerSavingSettings(
              itemModifier = Modifier.fillMaxWidth(),
              appName = appName,
              state = state,
              isTroubleshooting = isTroubleshooting,
              hasNotificationPermission = hasNotificationPermission,
              onOpenBatterySettings = onOpenBatterySettings,
              onRestartPowerService = onRestartPowerService,
              onTogglePowerSaving = onTogglePowerSaving,
              onToggleIgnoreInPowerSavingMode = onToggleIgnoreInPowerSavingMode,
              onToggleExitWhileCharging = onToggleExitWhileCharging,
              onStartTroubleshooting = { setTroubleShooting(true) },
              onDisableBatteryOptimization = onDisableBatteryOptimization,
              onRequestNotificationPermission = onRequestNotificationPermission,
          )
        } else {
          renderHomeSetupInstructions(
              itemModifier = Modifier.fillMaxWidth(),
              appName = appName,
              onCopy = onCopy,
              onRestartApp = onRestartApp,
          )
        }
      }

      item {
        Spacer(
            modifier =
                Modifier.padding(pv).navigationBarsPadding().height(MaterialTheme.keylines.content),
        )
      }
    }
  }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier.padding(MaterialTheme.keylines.content),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator(
        modifier = Modifier.widthIn(min = 64.dp).heightIn(min = 64.dp),
    )
  }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    appName: String,
    onOpenApplicationSettings: () -> Unit,
) {
  Box(
      modifier = modifier,
      contentAlignment = Alignment.CenterEnd,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center,
          text = appName,
          style =
              MaterialTheme.typography.h4.copy(
                  fontWeight = FontWeight.W700,
              ),
      )
    }

    IconButton(
        modifier = Modifier.padding(start = MaterialTheme.keylines.content),
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
private fun PreviewHomeScreen(state: HomeViewState) {
  HomeScreen(
      state = state,
      appName = "TEST",
      hasNotificationPermission = false,
      onToggleIgnoreInPowerSavingMode = {},
      onTogglePowerSaving = {},
      onOpenBatterySettings = {},
      onOpenApplicationSettings = {},
      onCopy = {},
      onRestartPowerService = {},
      onRestartApp = {},
      onToggleExitWhileCharging = {},
      onDisableBatteryOptimization = {},
      onRequestNotificationPermission = {},
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
      state =
          MutableHomeViewState().apply {
            hasPermission = true
            isPowerSettingsShortcutVisible = true
          },
  )
}
