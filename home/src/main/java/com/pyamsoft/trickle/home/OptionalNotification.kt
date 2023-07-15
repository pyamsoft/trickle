package com.pyamsoft.trickle.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.ui.icons.Label

private enum class NotificationTypes {
  LABEL,
  OPTION,
}

internal fun LazyListScope.renderNotificationSettings(
    itemModifier: Modifier = Modifier,
    state: HomeViewState,
    onRequest: () -> Unit,
) {
  item(
      contentType = NotificationTypes.LABEL,
  ) {
    Label(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
        text = "Notifications",
    )
  }

  item(
      contentType = NotificationTypes.OPTION,
  ) {
    val hasPermission by state.hasNotificationPermission.collectAsState()

    HomeOption(
        modifier = itemModifier,
        enabled = !hasPermission,
        checked = hasPermission,
        name = "Show Toggle Notification",
        description =
            """Keep the Service alive on newer Android versions.
            |
            |Without a notification, the Service may be stopped randomly."""
                .trimMargin(),
        onChange = { needsPermission ->
          if (needsPermission) {
            onRequest()
          }
        },
    )
  }
}
