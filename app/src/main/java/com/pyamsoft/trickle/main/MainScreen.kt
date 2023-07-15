package com.pyamsoft.trickle.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.trickle.home.HomeEntry
import com.pyamsoft.trickle.settings.SettingsDialog

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    appName: String,
    state: MainViewState,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
) {
  val permission by state.permission.collectAsState()
  val isSettingsOpen by state.isSettingsOpen.collectAsState()

  Scaffold(
      modifier = modifier.fillMaxSize(),
  ) { pv ->
    Column {
      Spacer(
          modifier = Modifier.padding(pv).statusBarsPadding(),
      )
      Header(
          modifier = Modifier.fillMaxWidth(),
          appName = appName,
          onOpenApplicationSettings = onOpenSettings,
      )

      when (permission) {
        MainViewState.PermissionState.NONE -> {
          Loading(
              modifier = Modifier.weight(1F).fillMaxWidth(),
          )
        }
        else -> {
          val hasPermission =
              remember(permission) { permission == MainViewState.PermissionState.GRANTED }
          HomeEntry(
              modifier = Modifier.weight(1F).fillMaxWidth(),
              appName = appName,
              hasPermission = hasPermission,
          )
        }
      }
    }
  }

  if (isSettingsOpen) {
    SettingsDialog(
        onDismiss = onCloseSettings,
    )
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
private fun Header(
    modifier: Modifier = Modifier,
    appName: String,
    onOpenApplicationSettings: () -> Unit,
) {
  val hapticManager = LocalHapticManager.current

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
        onClick = {
          hapticManager?.actionButtonPress()
          onOpenApplicationSettings()
        },
    ) {
      Icon(
          imageVector = Icons.Filled.Settings,
          contentDescription = "Open Settings",
      )
    }
  }
}

@Preview
@Composable
private fun PreviewMainScreen() {
  MainScreen(
      appName = "TEST",
      state = MutableMainViewState(),
      onOpenSettings = {},
      onCloseSettings = {},
  )
}