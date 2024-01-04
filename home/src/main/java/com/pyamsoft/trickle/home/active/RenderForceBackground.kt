package com.pyamsoft.trickle.home.active

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.home.HomeViewState

private enum class ForceBackgroundTypes {
  TITLE,
  PROMPT,
  ACTION,
}

internal fun LazyListScope.renderForceBackground(
    modifier: Modifier = Modifier,
    appName: String,
    state: HomeViewState,
    onForceBackground: () -> Unit,
) {
  item(
      contentType = ForceBackgroundTypes.TITLE,
  ) {
    Text(
        modifier = modifier.fillMaxWidth().padding(vertical = MaterialTheme.keylines.content),
        textAlign = TextAlign.Start,
        text = "Service Tweaks",
        style =
            MaterialTheme.typography.h6.copy(
                color =
                    MaterialTheme.colors.onBackground.copy(
                        alpha = ContentAlpha.high,
                    ),
            ),
    )
  }

  item(
      contentType = ForceBackgroundTypes.PROMPT,
  ) {
    Text(
        modifier = modifier,
        text =
            "On newer Android versions, the service is sometimes inconsistent. It may sometimes be fixed by forcing the application into the background. The \"Always Alive\" option must be enabled",
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
      contentType = ForceBackgroundTypes.ACTION,
  ) {
    val isEnabled by state.isBatteryOptimizationsIgnored.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.padding(top = MaterialTheme.keylines.content),
        contentAlignment = Alignment.Center,
    ) {
      Button(
          enabled = isEnabled,
          onClick = onForceBackground,
      ) {
        Text(
            text = "Force $appName Background",
        )
      }
    }
  }
}
