package com.pyamsoft.trickle.home

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pyamsoft.pydroid.ui.widget.ColoredMaterialCheckable

@Composable
internal fun HomeOption(
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    enabled: Boolean,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
  val colors = MaterialTheme.colors
  val selectedColor = remember(colors) { colors.secondary }

  ColoredMaterialCheckable(
      modifier = modifier,
      isEditable = enabled,
      condition = checked,
      title = name,
      description = description,
      selectedColor = selectedColor,
      onClick = { onChange(!checked) },
  )
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
