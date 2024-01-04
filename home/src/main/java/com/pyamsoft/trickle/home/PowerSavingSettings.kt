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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.theme.warning
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.trickle.ui.icons.Label

private enum class PowerContentTypes {
  MAIN_SWITCH,
  STAY_ALIVE,
  SPACER,
  OPEN_TROUBLESHOOTING,
  BOTTOM_SPACER
}

internal fun LazyListScope.renderPowerSavingSettings(
    itemModifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    showNotificationSettings: Boolean,
    isTroubleshooting: Boolean,
    onStartTroubleshooting: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onTogglePowerSaving: (Boolean) -> Unit,
    onDisableBatteryOptimization: () -> Unit,
    onRestartPowerService: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onForceBackground: () -> Unit,
) {

  item(
      contentType = PowerContentTypes.MAIN_SWITCH,
  ) {
    val isPowerSaving by state.isPowerSaving.collectAsStateWithLifecycle()

    HomeMainSwitch(
        modifier = itemModifier.padding(bottom = MaterialTheme.keylines.content * 2),
        enabled = isPowerSaving,
        onChange = onTogglePowerSaving,
    )
  }

  item(
      contentType = PowerContentTypes.STAY_ALIVE,
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

    Column(
        modifier = itemModifier.padding(vertical = MaterialTheme.keylines.content),
    ) {
      Label(
          text = "Operating Settings",
      )

      HomeOption(
          enabled = enabled,
          checked = isBatteryOptimizationsIgnored,
          name = "Always Alive",
          description =
              """This will allow the $appName Service to continue running even if the app is closed.
                  |
                  |For best performance, enable this option and then close the app completely by swiping it away. It should restart itself completely in the background and perform consistently after that.
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
  }

  if (showNotificationSettings) {
    renderNotificationSettings(
        itemModifier = itemModifier,
        state = state,
        onRequest = onRequestNotificationPermission,
    )

    item(
        contentType = PowerContentTypes.SPACER,
    ) {
      Spacer(
          modifier = Modifier.fillMaxWidth().height(MaterialTheme.keylines.content),
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
        onForceBackground = onForceBackground,
    )
  } else {
    item(
        contentType = PowerContentTypes.OPEN_TROUBLESHOOTING,
    ) {
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

  item(
      contentType = PowerContentTypes.BOTTOM_SPACER,
  ) {
    Spacer(
        modifier = itemModifier.height(MaterialTheme.keylines.content),
    )
  }
}

private enum class TroubleshootingTypes {
  LABEL,
  EXPLAIN,
  FORCE_BACKGROUND_PROMPT,
  FORCE_BACKGROUND,
  RESTART_SERVICE_PROMPT,
  RESTART_SERVICE,
  RESTART_SHORTCUT,
}

private fun LazyListScope.renderTroubleshooting(
    itemModifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    onRestartPowerService: () -> Unit,
    onOpenSettings: () -> Unit,
    onForceBackground: () -> Unit,
) {

  item(
      contentType = TroubleshootingTypes.LABEL,
  ) {
    Label(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
        text = "Troubleshooting",
    )
  }

  item(
      contentType = TroubleshootingTypes.EXPLAIN,
  ) {
    Text(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.typography),
        text =
            "If your device does not automatically manage power saving mode, the $appName Service may need to be reset.",
        style = MaterialTheme.typography.body1,
    )
  }

  item(
      contentType = TroubleshootingTypes.FORCE_BACKGROUND_PROMPT,
  ) {
    Text(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.typography),
        text =
            "Sometimes the service can be fixed by forcing the application into the background. The \"Always Alive\" option must be enabled",
        style =
            MaterialTheme.typography.caption.copy(
                color =
                    MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.medium,
                    ),
            ),
    )
  }

  item(
      contentType = TroubleshootingTypes.FORCE_BACKGROUND,
  ) {
    val isEnabled by state.isBatteryOptimizationsIgnored.collectAsStateWithLifecycle()

    Box(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
        contentAlignment = Alignment.Center,
    ) {
      Button(
          enabled = isEnabled,
          onClick = onForceBackground,
      ) {
        Text(
            text = "Force $appName Background",
        )
      }
    }
  }

  item(
      contentType = TroubleshootingTypes.RESTART_SERVICE_PROMPT,
  ) {
    Text(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
        text = "Otherwise click the button below a couple of times and see if that fixes things",
        style =
            MaterialTheme.typography.caption.copy(
                color =
                    MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.medium,
                    ),
            ),
    )
  }

  item(
      contentType = TroubleshootingTypes.RESTART_SERVICE,
  ) {
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

  item(
      contentType = TroubleshootingTypes.RESTART_SHORTCUT,
  ) {
    val isVisible by state.isPowerSettingsShortcutVisible.collectAsStateWithLifecycle()

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
        onTogglePowerSaving = {},
        onOpenBatterySettings = {},
        onRestartPowerService = {},
        onStartTroubleshooting = {},
        onDisableBatteryOptimization = {},
        onRequestNotificationPermission = {},
        onForceBackground = {},
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
