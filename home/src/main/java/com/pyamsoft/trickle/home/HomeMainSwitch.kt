package com.pyamsoft.trickle.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager

@Composable
internal fun HomeMainSwitch(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
  val hapticManager = LocalHapticManager.current

  Column(
      modifier = modifier,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          modifier = Modifier.weight(1F),
          text = "Automatic Power Saving",
          style = MaterialTheme.typography.h5,
      )

      Switch(
          checked = enabled,
          onCheckedChange = { newState ->
            if (newState) {
              hapticManager?.toggleOn()
            } else {
              hapticManager?.toggleOff()
            }
            onChange(newState)
          },
      )
    }

    Text(
        text =
            """
         When the screen turns off, enter power saving mode automatically.
         When the screen turns back on, exit power saving mode automatically.
      """
                .trimIndent(),
        style =
            MaterialTheme.typography.body2.copy(
                color =
                    MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.medium,
                    ),
            ),
    )
  }
}

@Composable
private fun PreviewHomeMainSwitch(enabled: Boolean) {
  HomeMainSwitch(
      enabled = enabled,
      onChange = {},
  )
}

@Preview
@Composable
private fun PreviewHomeMainSwitchOn() {
  PreviewHomeMainSwitch(
      enabled = true,
  )
}

@Preview
@Composable
private fun PreviewHomeMainSwitchOff() {
  PreviewHomeMainSwitch(
      enabled = false,
  )
}
