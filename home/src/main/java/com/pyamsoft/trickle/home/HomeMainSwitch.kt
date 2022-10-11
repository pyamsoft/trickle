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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.CardDefaults
import com.pyamsoft.trickle.ui.icons.LocalCafe

private const val TITLE = "Sip Power"

@Composable
internal fun HomeMainSwitch(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
) {
  val color = if (enabled) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface

  Box(
      modifier =
          modifier.border(
              width = 4.dp,
              color = color.copy(alpha = ContentAlpha.medium),
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
                  .clickable { onChange(!enabled) }
                  .padding(MaterialTheme.keylines.content),
      ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
              modifier = Modifier.size(32.dp),
              imageVector = Icons.Filled.LocalCafe,
              contentDescription = TITLE,
              tint = color.copy(alpha = ContentAlpha.high),
          )

          Text(
              modifier = Modifier.padding(start = MaterialTheme.keylines.content),
              text = TITLE,
              style =
                  MaterialTheme.typography.h5.copy(
                      color = color.copy(alpha = ContentAlpha.high),
                      fontWeight = FontWeight.W700,
                  ),
          )
        }

        Text(
            modifier = Modifier.padding(top = MaterialTheme.keylines.content),
            text = "When the screen turns off, enter power saving mode automatically.",
            style =
                MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                ),
        )
        Text(
            modifier = Modifier.padding(top = MaterialTheme.keylines.baseline),
            text = "When the screen turns back on, exit power saving mode automatically.",
            style =
                MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                ),
        )
      }
    }
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
