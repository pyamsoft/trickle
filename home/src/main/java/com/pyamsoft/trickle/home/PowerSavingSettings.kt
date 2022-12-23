package com.pyamsoft.trickle.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.ui.icons.Label

internal fun LazyListScope.renderPowerSavingSettings(
    itemModifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    isTroubleshooting: Boolean,
    hasNotificationPermission: Boolean,
    onStartTroubleshooting: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onTogglePowerSaving: (Boolean) -> Unit,
    onToggleIgnoreInPowerSavingMode: (Boolean) -> Unit,
    onToggleExitWhileCharging: (Boolean) -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
  val isPowerSaving = state.isPowerSaving

  item {
    HomeMainSwitch(
        modifier = itemModifier.padding(bottom = MaterialTheme.keylines.content * 2),
        enabled = isPowerSaving,
        onChange = onTogglePowerSaving,
    )
  }

  item {
    HomeOption(
        modifier = itemModifier.padding(bottom = MaterialTheme.keylines.content),
        name = "Play Nice",
        description =
            "$appName will not change settings if the device is already in power saving mode",
        enabled = isPowerSaving,
        checked = state.isIgnoreInPowerSavingMode,
        onChange = onToggleIgnoreInPowerSavingMode,
    )
  }

  item {
    HomeOption(
        modifier = itemModifier.padding(bottom = MaterialTheme.keylines.content),
        name = "Sip Power",
        description = "When the device is charging, $appName will exit power saving mode",
        enabled = isPowerSaving,
        checked = state.isExitWhileCharging,
        onChange = onToggleExitWhileCharging,
    )
  }

  item {
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
          checked = state.isBatteryOptimizationsIgnored,
          onChange = { onDisableBatteryOptimization() },
      )
    }
  }

  // Includes bottom space if rendered
  renderOptionalNotification(
      itemModifier = itemModifier,
      hasPermission = hasNotificationPermission,
      onRequest = onRequestNotificationPermission,
  )

  if (isTroubleshooting) {
    renderTroubleshooting(
        itemModifier = itemModifier,
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
                    contentColor = MaterialTheme.colors.error,
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
    state: HomeViewState,
    onRestartPowerService: () -> Unit,
    onOpenSettings: () -> Unit,
) {
  val isVisible = state.isPowerSettingsShortcutVisible

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
            "If your device does not automatically manage power saving mode, the Power Service may need to be reset.",
        style = MaterialTheme.typography.body1,
    )
  }

  item {
    Text(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.typography),
        text = "Click the button below a couple of times and see if that fixes things",
        color =
            MaterialTheme.colors.onBackground.copy(
                alpha = ContentAlpha.medium,
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
            text = "Restart Power Service",
        )
      }
    }
  }

  item {
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
                "Sometimes restarting the Power Service isn't enough, you'll need to change device Settings",
            style = MaterialTheme.typography.body1,
        )

        Text(
            modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
            text = "Click the button below to open the System Battery Settings screen.",
            style = MaterialTheme.typography.body1,
        )

        Text(
            modifier = Modifier.padding(top = MaterialTheme.keylines.typography),
            text = "Toggle Power Saving Mode on and off 3 times, this should fix the Power Service",
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
        hasNotificationPermission = false,
        onToggleIgnoreInPowerSavingMode = {},
        onTogglePowerSaving = {},
        onOpenBatterySettings = {},
        onRestartPowerService = {},
        onToggleExitWhileCharging = {},
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
      state = MutableHomeViewState().apply { hasPermission = true },
  )
}

@Preview
@Composable
private fun PreviewPowerSavingSettingsNoShortcut() {
  PreviewPowerSavingSettings(
      isTroubleshooting = true,
      state =
          MutableHomeViewState().apply {
            hasPermission = true
            isPowerSettingsShortcutVisible = false
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
            hasPermission = true
            isPowerSettingsShortcutVisible = true
          },
  )
}
