package com.pyamsoft.trickle.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.ui.icons.Label

internal fun LazyListScope.renderNotificationSettings(
    itemModifier: Modifier = Modifier,
    hasPermission: Boolean,
    onRequest: () -> Unit,
) {
  item {
    Label(
        modifier =
            itemModifier
                .padding(top = MaterialTheme.keylines.content)
                .padding(bottom = MaterialTheme.keylines.baseline),
        text = "Notifications",
    )
  }

  item {
    HomeOption(
        modifier = itemModifier,
        enabled = !hasPermission,
        checked = hasPermission,
        name = "Show Toggle Notification",
        description =
            """Show a notification with a toggle to quickly turn automatic power saving on and off
                |
                |Without a notification, the power-saving service may be stopped randomly.
            """
                .trimMargin(),
        onChange = { needsPermission ->
          if (needsPermission) {
            onRequest()
          }
        },
    )
  }
}
