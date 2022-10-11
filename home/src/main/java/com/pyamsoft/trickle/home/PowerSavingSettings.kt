package com.pyamsoft.trickle.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.theme.keylines

internal fun LazyListScope.renderPowerSavingSettings(
    state: HomeViewState,
    onOpenBatterySettings: () -> Unit,
    onTogglePowerSaving: (Boolean) -> Unit,
    onToggleIgnoreInPowerSavingMode: (Boolean) -> Unit,
    onToggleExitWhileCharging: (Boolean) -> Unit,
    onRestartPowerService: () -> Unit,
) {
  val isPowerSaving = state.isPowerSaving
  val isIgnoreInPowerSavingMode = state.isIgnoreInPowerSavingMode
  val isExitWhileCharging = state.isExitWhileCharging

  item {
    HomeMainSwitch(
        modifier = Modifier.fillMaxWidth(),
        enabled = isPowerSaving,
        onChange = onTogglePowerSaving,
    )
  }

  item {
    HomeOption(
        name = "Do nothing if already in power-saving mode",
        enabled = isIgnoreInPowerSavingMode,
        onChange = onToggleIgnoreInPowerSavingMode,
    )
  }

  item {
    HomeOption(
        name = "Exit power-saving mode while charging",
        enabled = isExitWhileCharging,
        onChange = onToggleExitWhileCharging,
    )
  }

  renderGoToSettings(
      state = state,
      onOpenSettings = onOpenBatterySettings,
      onRestartPowerService = onRestartPowerService,
  )
}

private fun LazyListScope.renderGoToSettings(
    state: HomeViewState,
    onRestartPowerService: () -> Unit,
    onOpenSettings: () -> Unit,
) {
  val isVisible = state.isPowerSettingsShortcutVisible

  item {
    Text(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
        text = "If it doesn't work, click the button below",
        style = MaterialTheme.typography.body1,
    )
  }

  item {
    Button(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
        onClick = onRestartPowerService,
    ) {
      Text(
          text = "Restart Power Service",
      )
    }
  }

  item {
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content),
        visible = isVisible,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = "If it still doesn't work, open the settings and toggle the mode a couple times",
            style = MaterialTheme.typography.body1,
        )

        Button(
            modifier = Modifier.padding(top = MaterialTheme.keylines.content),
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
