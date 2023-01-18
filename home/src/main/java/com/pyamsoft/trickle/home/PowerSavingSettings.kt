package com.pyamsoft.trickle.home

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.theme.warning
import com.pyamsoft.trickle.ui.icons.Label

internal fun LazyListScope.renderPowerSavingSettings(
    itemModifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    showNotificationSettings: Boolean,
    isTroubleshooting: Boolean,
    hasNotificationPermission: Boolean,
    onStartTroubleshooting: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onTogglePowerSaving: (Boolean) -> Unit,
    onToggleIgnoreInPowerSavingMode: (Boolean) -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {

  item {
    val isPowerSaving by state.isPowerSaving.collectAsState()

    HomeMainSwitch(
        modifier = itemModifier.padding(bottom = MaterialTheme.keylines.content * 2),
        enabled = isPowerSaving,
        onChange = onTogglePowerSaving,
    )
  }

  item {
    val isPowerSaving by state.isPowerSaving.collectAsState()
    val isIgnoreInPowerSavingMode by state.isIgnoreInPowerSavingMode.collectAsState()

    HomeOption(
        modifier = itemModifier.padding(bottom = MaterialTheme.keylines.content),
        name = "Play Nice",
        description =
            "$appName will not change settings if the device is already in power saving mode",
        enabled = isPowerSaving,
        checked = isIgnoreInPowerSavingMode,
        onChange = onToggleIgnoreInPowerSavingMode,
    )
  }

  item {
    val isPowerSaving by state.isPowerSaving.collectAsState()
    val isBatteryOptimizationsIgnored by state.isBatteryOptimizationsIgnored.collectAsState()

    Column(
        modifier = itemModifier.padding(vertical = MaterialTheme.keylines.content),
    ) {
      Label(
          modifier = Modifier.padding(bottom = MaterialTheme.keylines.baseline),
          text = "Battery",
      )
      HomeOption(
          name = "Ignore Battery Optimizations",
          description =
              """This allows $appName to run all the time.
                |
                |Provides the most reliable experience, and will help you save more battery, despite being "unoptimized".
                |(recommended)
            """
                  .trimMargin(),
          enabled = isPowerSaving,
          checked = isBatteryOptimizationsIgnored,
          onChange = { onDisableBatteryOptimization() },
      )
    }
  }

  if (showNotificationSettings) {
    renderNotificationSettings(
        itemModifier = itemModifier,
        hasPermission = hasNotificationPermission,
        onRequest = onRequestNotificationPermission,
    )

    item {
      Spacer(
          modifier =
              Modifier.fillMaxWidth()
                  .padding(horizontal = MaterialTheme.keylines.content)
                  .height(MaterialTheme.keylines.content),
      )
    }
  }

  if (isTroubleshooting) {
    renderTroubleshooting(
        itemModifier = itemModifier,
        appName = appName,
        state = state,
        onRestartPowerService = onRestartPowerService,
        onOpenSettings = onOpenBatterySettings,
    )
  } else {
    item {
      Box(
          modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
          contentAlignment = Alignment.Center,
      ) {
        OutlinedButton(
            onClick = onStartTroubleshooting,
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colors.warning,
                ),
        ) {
          Text(
              text = "View Troubleshooting Instructions",
          )
        }
      }
    }
  }

  item {
    Spacer(
        modifier = itemModifier.height(MaterialTheme.keylines.content),
    )
  }
}

private fun LazyListScope.renderTroubleshooting(
    itemModifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    onRestartPowerService: () -> Unit,
    onOpenSettings: () -> Unit,
) {

  item {
    Label(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
        text = "Troubleshooting",
    )
  }

  item {
    Text(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.typography),
        text =
            "If your device does not automatically manage power saving mode, the $appName Service may need to be reset.",
        style = MaterialTheme.typography.body1,
    )
  }

  item {
    Text(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.typography),
        text = "Click the button below a couple of times and see if that fixes things",
        style =
            MaterialTheme.typography.caption.copy(
                color =
                    MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.medium,
                    ),
            ),
    )
  }

  item {
    Box(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
        contentAlignment = Alignment.Center,
    ) {
      Button(
          onClick = onRestartPowerService,
      ) {
        Text(
            text = "Restart $appName Service",
        )
      }
    }
  }

  item {
    val isVisible by state.isPowerSettingsShortcutVisible.collectAsState()

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(),
        exit = slideOutVertically() + fadeOut(),
    ) {
      Column(
          modifier = itemModifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content * 2),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text =
                "Sometimes restarting $appName Service isn't enough, and you'll need to change device Settings.",
            style = MaterialTheme.typography.body1,
        )

        Text(
            modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
            text = "Click the button below to open the System Battery Settings screen.",
            style = MaterialTheme.typography.body1,
        )

        Text(
            modifier = Modifier.padding(top = MaterialTheme.keylines.typography),
            text =
                "Toggle Power-Saving Mode on and off 3 times, this should fix the $appName Service.",
            style = MaterialTheme.typography.body1,
        )

        Box(
            modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content),
            contentAlignment = Alignment.Center,
        ) {
          Button(
              onClick = onOpenSettings,
          ) {
            Text(
                text = "Open System Battery Settings",
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PreviewPowerSavingSettings(
    state: HomeViewState,
    isTroubleshooting: Boolean,
) {
  LazyColumn {
    renderPowerSavingSettings(
        appName = "TEST",
        state = state,
        isTroubleshooting = isTroubleshooting,
        showNotificationSettings = false,
        hasNotificationPermission = false,
        onToggleIgnoreInPowerSavingMode = {},
        onTogglePowerSaving = {},
        onOpenBatterySettings = {},
        onRestartPowerService = {},
        onStartTroubleshooting = {},
        onDisableBatteryOptimization = {},
        onRequestNotificationPermission = {},
    )
  }
}

@Preview
@Composable
private fun PreviewPowerSavingSettingsNoTrouble() {
  PreviewPowerSavingSettings(
      isTroubleshooting = false,
      state =
          MutableHomeViewState().apply {
            permissionState.value = HomeViewState.PermissionState.GRANTED
          },
  )
}

@Preview
@Composable
private fun PreviewPowerSavingSettingsNoShortcut() {
  PreviewPowerSavingSettings(
      isTroubleshooting = true,
      state =
          MutableHomeViewState().apply {
            permissionState.value = HomeViewState.PermissionState.GRANTED
            isPowerSettingsShortcutVisible.value = false
          },
  )
}

@Preview
@Composable
private fun PreviewPowerSavingSettingsWithShortcut() {
  PreviewPowerSavingSettings(
      isTroubleshooting = true,
      state =
          MutableHomeViewState().apply {
            permissionState.value = HomeViewState.PermissionState.GRANTED
            isPowerSettingsShortcutVisible.value = true
          },
  )
}
