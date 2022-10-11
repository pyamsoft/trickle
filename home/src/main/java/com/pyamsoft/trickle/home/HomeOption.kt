package com.pyamsoft.trickle.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.CardDefaults

@Composable
internal fun HomeOption(
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    enabled: Boolean,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
  val color = if (checked) MaterialTheme.colors.secondary else MaterialTheme.colors.onSurface
  val highAlpha =
      if (enabled) {
        if (checked) ContentAlpha.high else ContentAlpha.medium
      } else ContentAlpha.disabled
  val mediumAlpha =
      if (enabled) {
        if (checked) ContentAlpha.medium else ContentAlpha.disabled
      } else ContentAlpha.disabled

  Box(
      modifier =
          modifier.border(
              width = 2.dp,
              color = color.copy(alpha = mediumAlpha),
              shape = MaterialTheme.shapes.medium,
          ),
  ) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.Elevation,
    ) {
      Column(
          modifier =
              Modifier.fillMaxWidth()
                  .clickable(enabled = enabled) { onChange(!checked) }
                  .padding(MaterialTheme.keylines.content),
      ) {
        Row(
            modifier = modifier.fillMaxWidth().padding(bottom = MaterialTheme.keylines.baseline),
            verticalAlignment = Alignment.Top,
        ) {
          Text(
              modifier = Modifier.weight(1F),
              text = name,
              style =
                  MaterialTheme.typography.body1.copy(
                      fontWeight = FontWeight.W700,
                      color = color.copy(alpha = highAlpha),
                  ),
          )

          Icon(
              modifier = Modifier.size(20.dp),
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = name,
              tint = color.copy(alpha = mediumAlpha),
          )
        }

        Text(
            text = description,
            style =
                MaterialTheme.typography.caption.copy(
                    color = color.copy(alpha = mediumAlpha),
                    fontWeight = FontWeight.W400,
                ),
        )
      }
    }
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
