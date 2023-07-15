package com.pyamsoft.trickle.home

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.ui.renderPYDroidExtras

private enum class HomeContentTypes {
  SPACER,
  LOADING,
  BOTTOM_SPACER,
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeViewState,
    appName: String,
    hasPermission: Boolean,
    onTogglePowerSaving: (Boolean) -> Unit,
    onCopy: (String) -> Unit,
    onOpenBatterySettings: () -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRestartApp: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onOpenTroubleshooting: () -> Unit,
) {
  val showNotificationSettings = remember { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU }

  val loadingState by state.loadingState.collectAsState()
  val isTroubleshooting by state.isTroubleshooting.collectAsState()

  Scaffold(
      modifier = modifier,
  ) { pv ->
    LazyColumn(
        contentPadding = PaddingValues(horizontal = MaterialTheme.keylines.content),
    ) {
      renderPYDroidExtras()

      item(
          contentType = HomeContentTypes.SPACER,
      ) {
        Spacer(
            modifier = Modifier.height(MaterialTheme.keylines.content),
        )
      }

      when (loadingState) {
        HomeViewState.LoadingState.NONE,
        HomeViewState.LoadingState.LOADING -> {
          item(
              contentType = HomeContentTypes.LOADING,
          ) {
            Loading(
                modifier = Modifier.fillMaxWidth(),
            )
          }
        }
        HomeViewState.LoadingState.DONE -> {
          if (hasPermission) {
            renderPowerSavingSettings(
                itemModifier = Modifier.fillMaxWidth(),
                appName = appName,
                state = state,
                showNotificationSettings = showNotificationSettings,
                isTroubleshooting = isTroubleshooting,
                onOpenBatterySettings = onOpenBatterySettings,
                onRestartPowerService = onRestartPowerService,
                onTogglePowerSaving = onTogglePowerSaving,
                onStartTroubleshooting = onOpenTroubleshooting,
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
      }

      item(
          contentType = HomeContentTypes.BOTTOM_SPACER,
      ) {
        Spacer(
            modifier = Modifier.padding(pv).navigationBarsPadding(),
        )
      }
    }
  }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier,
) {
  val size = 64.dp

  Box(
      modifier = modifier.padding(MaterialTheme.keylines.content),
      contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator(
        modifier =
            Modifier.sizeIn(
                minWidth = size,
                minHeight = size,
            ),
    )
  }
}

@Composable
private fun PreviewHomeScreen(state: HomeViewState, hasPermission: Boolean) {
  HomeScreen(
      state = state,
      appName = "TEST",
      hasPermission = hasPermission,
      onOpenTroubleshooting = {},
      onTogglePowerSaving = {},
      onOpenBatterySettings = {},
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
      hasPermission = false,
      state = MutableHomeViewState(),
  )
}

@Preview
@Composable
private fun PreviewHomeScreenHasPermissionNoShortcut() {
  PreviewHomeScreen(
      hasPermission = true,
      state = MutableHomeViewState().apply { isPowerSettingsShortcutVisible.value = false },
  )
}

@Preview
@Composable
private fun PreviewHomeScreenHasPermissionWithShortcut() {
  PreviewHomeScreen(
      hasPermission = true,
      state = MutableHomeViewState().apply { isPowerSettingsShortcutVisible.value = true },
  )
}
