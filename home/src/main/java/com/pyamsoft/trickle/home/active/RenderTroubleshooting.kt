package com.pyamsoft.trickle.home.active

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.theme.warning
import com.pyamsoft.trickle.home.HomeViewState
import com.pyamsoft.trickle.ui.icons.Label

private enum class TroubleshootingTypes {
  LABEL,
  EXPLAIN,
  RESTART_SERVICE_PROMPT,
  RESTART_SERVICE,
  RESTART_SHORTCUT,
  ACTIVE
}

private fun LazyListScope.renderExplainTroubleshooting(
    modifier: Modifier = Modifier,
    onStartTroubleshooting: () -> Unit,
) {
  item(
      contentType = TroubleshootingTypes.ACTIVE,
  ) {
    Box(
        modifier = modifier,
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

private fun LazyListScope.renderActiveTroubleshooting(
    modifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    onRestartPowerService: () -> Unit,
    onOpenSettings: () -> Unit,
) {
  item(
      contentType = TroubleshootingTypes.LABEL,
  ) {
    Label(
        modifier = modifier,
        text = "Troubleshooting",
    )
  }

  item(
      contentType = TroubleshootingTypes.EXPLAIN,
  ) {
    Text(
        modifier = modifier.padding(top = MaterialTheme.keylines.typography),
        text =
            "If your device does not automatically manage power saving mode, the $appName Service may need to be reset.",
        style = MaterialTheme.typography.body1,
    )
  }

  item(
      contentType = TroubleshootingTypes.RESTART_SERVICE_PROMPT,
  ) {
    Text(
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
        text =
            "Click the button below a couple of times and see if that fixes things. You may also want to try the \"Force $appName Background\" tweak.",
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
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
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
          modifier = modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content * 2),
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

internal fun LazyListScope.renderTroubleshooting(
    modifier: Modifier = Modifier,
    isTroubleshooting: Boolean,
    appName: String,
    state: HomeViewState,
    onRestartPowerService: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartTroubleshooting: () -> Unit,
) {
  if (isTroubleshooting) {
    renderActiveTroubleshooting(
        modifier = modifier,
        appName = appName,
        state = state,
        onRestartPowerService = onRestartPowerService,
        onOpenSettings = onOpenSettings,
    )
  } else {
    renderExplainTroubleshooting(
        modifier = modifier,
        onStartTroubleshooting = onStartTroubleshooting,
    )
  }
}
