package com.pyamsoft.trickle.home.active

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.home.HomeViewState
import com.pyamsoft.trickle.home.MutableHomeViewState

private enum class PowerContentTypes {
  SPACER,
}

internal fun LazyListScope.renderPowerSavingSettings(
    itemModifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    showNotificationSettings: Boolean,
    mayNeedForceBackground: Boolean,
    isTroubleshooting: Boolean,
    onStartTroubleshooting: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onTogglePowerSaving: (Boolean) -> Unit,
    onToggleForceBackground: (Boolean) -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onForceBackground: () -> Unit,
) {
  renderMainControls(
      modifier = itemModifier,
      state = state,
      onTogglePowerSaving = onTogglePowerSaving,
  )

  renderSpacer(
      modifier = itemModifier,
  )

  renderOperatingSettings(
      modifier = itemModifier,
      state = state,
      appName = appName,
      onToggleForceBackground = onToggleForceBackground,
      onDisableBatteryOptimization = onDisableBatteryOptimization,
  )

  renderSpacer(
      modifier = itemModifier,
  )

  if (showNotificationSettings) {
    renderNotificationSettings(
        itemModifier = itemModifier,
        state = state,
        onRequest = onRequestNotificationPermission,
    )

    renderSpacer(
        modifier = itemModifier,
    )
  }

  if (mayNeedForceBackground || isTroubleshooting) {
    renderForceBackground(
        modifier = itemModifier,
        appName = appName,
        state = state,
        onForceBackground = onForceBackground,
    )

    renderSpacer(
        modifier = itemModifier,
    )
  }

  renderTroubleshooting(
      modifier = itemModifier,
      isTroubleshooting = isTroubleshooting,
      appName = appName,
      state = state,
      onRestartPowerService = onRestartPowerService,
      onOpenSettings = onOpenBatterySettings,
      onStartTroubleshooting = onStartTroubleshooting,
  )

  renderSpacer(
      modifier = itemModifier,
  )
}

private fun LazyListScope.renderSpacer(
    modifier: Modifier = Modifier,
) {
  item(
      contentType = PowerContentTypes.SPACER,
  ) {
    Spacer(
        modifier = modifier.height(MaterialTheme.keylines.content),
    )
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
        mayNeedForceBackground = true,
        showNotificationSettings = false,
        onTogglePowerSaving = {},
        onOpenBatterySettings = {},
        onRestartPowerService = {},
        onStartTroubleshooting = {},
        onDisableBatteryOptimization = {},
        onRequestNotificationPermission = {},
        onForceBackground = {},
        onToggleForceBackground = {},
    )
  }
}

@Preview
@Composable
private fun PreviewPowerSavingSettingsNoTrouble() {
  PreviewPowerSavingSettings(
      isTroubleshooting = false,
      state = MutableHomeViewState(),
  )
}

@Preview
@Composable
private fun PreviewPowerSavingSettingsNoShortcut() {
  PreviewPowerSavingSettings(
      isTroubleshooting = true,
      state = MutableHomeViewState().apply { isPowerSettingsShortcutVisible.value = false },
  )
}

@Preview
@Composable
private fun PreviewPowerSavingSettingsWithShortcut() {
  PreviewPowerSavingSettings(
      isTroubleshooting = true,
      state = MutableHomeViewState().apply { isPowerSettingsShortcutVisible.value = true },
  )
}
