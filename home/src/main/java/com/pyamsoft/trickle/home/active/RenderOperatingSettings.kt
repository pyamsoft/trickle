package com.pyamsoft.trickle.home.active

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.trickle.home.HomeViewState
import com.pyamsoft.trickle.ui.icons.Label

private enum class RenderOperatingSettingsContentTypes {
  LABEL,
  ALWAYS_ALIVE,
  FORCE_BACKGROUND
}

fun LazyListScope.renderOperatingSettings(
    modifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    onToggleForceBackground: (Boolean) -> Unit,
    onDisableBatteryOptimization: () -> Unit,
) {
  item(
      contentType = RenderOperatingSettingsContentTypes.LABEL,
  ) {
    Label(
        modifier = modifier,
        text = "Operating Settings",
    )
  }

  item(
      contentType = RenderOperatingSettingsContentTypes.ALWAYS_ALIVE,
  ) {
    val hapticManager = LocalHapticManager.current
    val isPowerSaving by state.isPowerSaving.collectAsStateWithLifecycle()
    val isBatteryOptimizationsIgnored by
        state.isBatteryOptimizationsIgnored.collectAsStateWithLifecycle()

    val enabled =
        remember(
            isPowerSaving,
            isBatteryOptimizationsIgnored,
        ) {
          isPowerSaving && !isBatteryOptimizationsIgnored
        }

    HomeOption(
        modifier = modifier,
        enabled = enabled,
        checked = isBatteryOptimizationsIgnored,
        name = "Always Alive",
        description =
            """This will allow the $appName Service to continue running even if the app is closed.
                  |
                  |For best performance, enable this option and then use the "Force $appName Background" tweak.
                 |(recommended)"""
                .trimMargin(),
        onChange = {
          if (!isBatteryOptimizationsIgnored) {
            hapticManager?.confirmButtonPress()
            onDisableBatteryOptimization()
          }
        },
    )
  }

  item(
      contentType = RenderOperatingSettingsContentTypes.FORCE_BACKGROUND,
  ) {
    val hapticManager = LocalHapticManager.current
    val isPowerSaving by state.isPowerSaving.collectAsStateWithLifecycle()
    val isBatteryOptimizationsIgnored by
        state.isBatteryOptimizationsIgnored.collectAsStateWithLifecycle()
    val isForceBackgroundEnabled by state.isAlwaysForceBackground.collectAsStateWithLifecycle()

    val canForceBackground =
        remember(
            isPowerSaving,
            isBatteryOptimizationsIgnored,
        ) {
          isPowerSaving && isBatteryOptimizationsIgnored
        }

    HomeOption(
        modifier = modifier,
        enabled = canForceBackground,
        checked = isForceBackgroundEnabled,
        name = "Always Force Background",
        description =
            """Every time you close the UI (home button, back button), $appName will force itself into the background.
                |
                |This can mess with the expected UI a little bit (the Android Lifecycle), but can generally provide more reliable service performance.
            """
                .trimMargin(),
        onChange = {
          hapticManager?.confirmButtonPress()
          onToggleForceBackground(it)
        },
    )
  }
}
