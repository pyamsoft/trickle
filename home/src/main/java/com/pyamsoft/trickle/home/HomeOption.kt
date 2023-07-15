package com.pyamsoft.trickle.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager

@Composable
internal fun HomeOption(
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    enabled: Boolean,
    checked: Boolean,
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
          text = name,
          style = MaterialTheme.typography.h6,
      )

      Checkbox(
          enabled = enabled,
          checked = checked,
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
        text = description,
        style =
            MaterialTheme.typography.caption.copy(
                color =
                    MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.medium,
                    ),
            ),
    )
  }
}

@Composable
private fun PreviewHomeOption(enabled: Boolean, checked: Boolean) {
  HomeOption(
      enabled = enabled,
      checked = checked,
      name = "Testing",
      description = "This is just for show you know",
      onChange = {},
  )
}

@Preview
@Composable
private fun PreviewHomeOptionOn() {
  PreviewHomeOption(
      enabled = true,
      checked = true,
  )
}

@Preview
@Composable
private fun PreviewHomeOptionOff() {
  PreviewHomeOption(
      enabled = true,
      checked = false,
  )
}

@Preview
@Composable
private fun PreviewHomeOptionDisabled() {
  PreviewHomeOption(
      enabled = false,
      checked = false,
  )
}
