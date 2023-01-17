package com.pyamsoft.trickle.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pyamsoft.trickle.home.HomeEntry

@Composable
fun MainEntry(
    modifier: Modifier = Modifier,
    appName: String,
) {
  Scaffold(
      modifier = modifier.fillMaxSize(),
  ) { pv ->
    HomeEntry(
        modifier = Modifier.fillMaxSize().padding(pv),
        appName = appName,
    )
  }
}
