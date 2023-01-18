package com.pyamsoft.trickle.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.pyamsoft.trickle.home.HomeEntry
import com.pyamsoft.trickle.settings.SettingsDialog

@Composable
fun MainEntry(
    modifier: Modifier = Modifier,
    appName: String,
    state: MainViewState,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
) {
  val showDialog by state.isSettingsOpen.collectAsState()

  Scaffold(
      modifier = modifier.fillMaxSize(),
  ) { pv ->
    HomeEntry(
        modifier = Modifier.fillMaxSize().padding(pv),
        appName = appName,
        onOpenSettings = onOpenSettings,
    )
  }

  if (showDialog) {
    SettingsDialog(
        onDismiss = onCloseSettings,
    )
  }
}
