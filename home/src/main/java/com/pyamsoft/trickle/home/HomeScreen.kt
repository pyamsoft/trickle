package com.pyamsoft.trickle.home

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onCopy: (String) -> Unit,
    onOpenBatterySettings: () -> Unit,
    onOpenApplicationSettings: () -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRestartApp: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onOpenTroubleshooting: () -> Unit,
) {
  val showNotificationSettings = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU }

  val permissionState by state.permissionState.collectAsState()
  val loadingState by state.loadingState.collectAsState()
  val isTroubleshooting by state.isTroubleshooting.collectAsState()

  Scaffold(
      modifier = modifier,
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
            modifier = Modifier.fillMaxWidth(),
            appName = appName,
            onOpenApplicationSettings = onOpenApplicationSettings,
        )
      }

      renderPYDroidExtras()

      item {
        Spacer(
            modifier = Modifier.height(MaterialTheme.keylines.content),
        )
      }

      when (loadingState) {
        HomeViewState.LoadingState.NONE,
        HomeViewState.LoadingState.LOADING -> {
          item {
            Loading(
                modifier = Modifier.fillMaxWidth(),
            )
          }
        }
        HomeViewState.LoadingState.DONE -> {
          when (permissionState) {
            HomeViewState.PermissionState.NONE -> {
              item {
                Loading(
                    modifier = Modifier.fillMaxWidth(),
                )
              }
            }
            HomeViewState.PermissionState.GRANTED -> {
              renderPowerSavingSettings(
                  itemModifier = Modifier.fillMaxWidth(),
                  appName = appName,
                  state = state,
                  showNotificationSettings = showNotificationSettings,
                  isTroubleshooting = isTroubleshooting,
                  hasNotificationPermission = hasNotificationPermission,
                  onOpenBatterySettings = onOpenBatterySettings,
                  onRestartPowerService = onRestartPowerService,
                  onTogglePowerSaving = onTogglePowerSaving,
                  onToggleIgnoreInPowerSavingMode = onToggleIgnoreInPowerSavingMode,
                  onStartTroubleshooting = onOpenTroubleshooting,
                  onDisableBatteryOptimization = onDisableBatteryOptimization,
                  onRequestNotificationPermission = onRequestNotificationPermission,
              )
            }
            HomeViewState.PermissionState.DENIED -> {
              renderHomeSetupInstructions(
                  itemModifier = Modifier.fillMaxWidth(),
                  appName = appName,
                  onCopy = onCopy,
                  onRestartApp = onRestartApp,
              )
            }
          }
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
      onOpenTroubleshooting = {},
      onTogglePowerSaving = {},
      onOpenBatterySettings = {},
      onOpenApplicationSettings = {},
      onCopy = {},
      onRestartPowerService = {},
      onRestartApp = {},
      onDisableBatteryOptimization = {},
      onRequestNotificationPermission = {},
  )
}

@Preview
@Composable
private fun PreviewHomeScreenNoPermission() {
  PreviewHomeScreen(
      state =
          MutableHomeViewState().apply {
            permissionState.value = HomeViewState.PermissionState.DENIED
          },
  )
}

@Preview
@Composable
private fun PreviewHomeScreenHasPermission() {
  PreviewHomeScreen(
      state =
          MutableHomeViewState().apply {
            permissionState.value = HomeViewState.PermissionState.GRANTED
            isPowerSettingsShortcutVisible.value = true
          },
  )
}
