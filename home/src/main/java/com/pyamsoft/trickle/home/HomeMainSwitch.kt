package com.pyamsoft.trickle.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.ui.widget.MaterialCheckable

@Composable
internal fun HomeMainSwitch(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
  MaterialCheckable(
      modifier = modifier,
      isEditable = true,
      condition = enabled,
      title = "Sip Power",
      description =
          """
         When the screen turns off, enter power saving mode automatically. 
         When the screen turns back on, exit power saving mode automatically.
      """
              .trimIndent(),
      onClick = { onChange(!enabled) },
  )
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
