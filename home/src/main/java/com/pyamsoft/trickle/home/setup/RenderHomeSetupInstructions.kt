package com.pyamsoft.trickle.home.setup

import android.content.Context
import androidx.annotation.CheckResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.CardDefaults
import com.pyamsoft.pydroid.ui.haptics.LocalHapticManager
import com.pyamsoft.pydroid.ui.theme.ZeroSize
import com.pyamsoft.pydroid.ui.uri.rememberUriHandler
import com.pyamsoft.trickle.ui.icons.Devices
import com.pyamsoft.trickle.ui.icons.PhoneAndroid

private enum class InstructionContentTypes {
  THIS_DEVICE_EXPLAINER,
  OTHER_DEVICE_EXPLAINER,
  DOWNLOAD_ADB,
  ENABLE_DEVELOPER_MODE,
  ACCEPT_PROMPT,
  GRANT_PERMISSION,
  ADB_COPY_COMMAND,
  RESTART_APP,
}

private val thisDeviceColor = Color(0xFF4CAF50)
private val otherDeviceColor = Color(0xFF2196F3)

@Composable
private fun ThisDevice(
    modifier: Modifier = Modifier,
    small: Boolean,
) {
  Icon(
      modifier =
          modifier
              .padding(start = if (small) 2.dp else ZeroSize)
              .size(if (small) 20.dp else 24.dp)
              .padding(end = if (small) 2.dp else ZeroSize),
      imageVector = Icons.Filled.PhoneAndroid,
      contentDescription = "This Device",
      tint = thisDeviceColor,
  )
}

@Composable
private fun OtherDevice(
    modifier: Modifier = Modifier,
    small: Boolean,
) {
  Icon(
      modifier =
          modifier
              .padding(start = if (small) 2.dp else ZeroSize)
              .size(if (small) 20.dp else 24.dp)
              .padding(end = if (small) 2.dp else ZeroSize),
      imageVector = Icons.Filled.Devices,
      contentDescription = "Laptop or Desktop",
      tint = otherDeviceColor,
  )
}

@Composable
private fun ThisInstruction(
    modifier: Modifier = Modifier,
    small: Boolean = false,
    content: @Composable () -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
      ThisDevice(small = small)
    }

    Box(
        modifier = Modifier.weight(1F).padding(start = MaterialTheme.keylines.content),
    ) {
      content()
    }
  }
}

@Composable
private fun OtherInstruction(
    modifier: Modifier = Modifier,
    small: Boolean = false,
    content: @Composable () -> Unit,
) {
  Row(
      modifier = modifier,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
      OtherDevice(small = small)
    }

    Box(
        modifier = Modifier.weight(1F).padding(start = MaterialTheme.keylines.content),
    ) {
      content()
    }
  }
}

private const val ADB_URL = "https://developer.android.com/studio/releases/platform-tools"
private const val ADB_LINK_TEXT = "Android Debug Bridge (ADB)"
private const val ADB_URI_TAG = "adb"

private inline fun AnnotatedString.Builder.withStringAnnotation(
    tag: String,
    annotation: String,
    content: () -> Unit
) {
  pushStringAnnotation(tag = tag, annotation = annotation)
  content()
  pop()
}

private fun onTextClicked(
    text: AnnotatedString,
    uriHandler: UriHandler,
    start: Int,
    tag: String,
    linkText: String,
) {
  text
      .getStringAnnotations(
          tag = tag,
          start = start,
          end = start + linkText.length,
      )
      .firstOrNull()
      ?.also { uriHandler.openUri(it.item) }
}

@Composable
private fun DownloadAdb(
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier,
  ) {
    val text = buildAnnotatedString {
      withStyle(
          style =
              SpanStyle(
                  color = MaterialTheme.colors.onBackground,
              ),
      ) {
        append("Download ")
      }

      withStringAnnotation(
          tag = ADB_URI_TAG,
          annotation = ADB_URL,
      ) {
        withStyle(
            style =
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colors.secondary,
                ),
        ) {
          append(ADB_LINK_TEXT)
        }
      }

      withStyle(
          style =
              SpanStyle(
                  color = MaterialTheme.colors.onBackground,
              ),
      ) {
        append(" and install it on your Laptop or Desktop machine.")
      }
    }

    val uriHandler = rememberUriHandler()
    ClickableText(
        text = text,
        style = MaterialTheme.typography.body1,
        onClick = {
          onTextClicked(
              text = text,
              uriHandler = uriHandler,
              start = it,
              tag = ADB_URI_TAG,
              linkText = ADB_LINK_TEXT,
          )
        },
    )
  }
}

private const val DEV_SETTINGS_URL = "https://developer.android.com/studio/debug/dev-options"
private const val DEV_SETTINGS_LINK_TEXT = "Developer Mode"
private const val DEV_SETTINGS_URI_TAG = "dev settings"

@Composable
private fun EnableDeveloperSettings(
    modifier: Modifier = Modifier,
) {
  Box(
      modifier = modifier,
  ) {
    val text = buildAnnotatedString {
      withStyle(
          style =
              SpanStyle(
                  color = MaterialTheme.colors.onBackground,
              ),
      ) {
        append("Enable ")
      }
      withStringAnnotation(
          tag = DEV_SETTINGS_URI_TAG,
          annotation = DEV_SETTINGS_URL,
      ) {
        withStyle(
            style =
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colors.secondary,
                ),
        ) {
          append(DEV_SETTINGS_LINK_TEXT)
        }
      }

      withStyle(
          style =
              SpanStyle(
                  color = MaterialTheme.colors.onBackground,
              ),
      ) {
        append(" on your device")
      }
    }

    val uriHandler = rememberUriHandler()
    ClickableText(
        text = text,
        style = MaterialTheme.typography.body1,
        onClick = {
          onTextClicked(
              text = text,
              uriHandler = uriHandler,
              start = it,
              tag = DEV_SETTINGS_URI_TAG,
              linkText = DEV_SETTINGS_LINK_TEXT,
          )
        },
    )
  }
}

@CheckResult
private fun createAdbCommand(context: Context, permission: String): String {
  return "adb shell pm grant ${context.packageName} $permission"
}

@Composable
@CheckResult
private fun rememberAdbCommand(permission: String): String {
  val context = LocalContext.current
  return remember(context, permission) { createAdbCommand(context, permission) }
}

internal fun LazyListScope.renderSetupInstructions(
    itemModifier: Modifier = Modifier,
    appName: String,
    adbPermission: String,
    onCopy: (String) -> Unit,
    onRestartApp: () -> Unit,
) {
  item(
      contentType = InstructionContentTypes.THIS_DEVICE_EXPLAINER,
  ) {
    ThisInstruction(
        modifier = itemModifier,
        small = true,
    ) {
      Text(
          text = "This Device",
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

  item(
      contentType = InstructionContentTypes.OTHER_DEVICE_EXPLAINER,
  ) {
    OtherInstruction(
        modifier = itemModifier,
        small = true,
    ) {
      Text(
          text = "Laptop or Desktop",
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

  item(
      contentType = InstructionContentTypes.DOWNLOAD_ADB,
  ) {
    OtherInstruction(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
    ) {
      DownloadAdb()
    }
  }

  item(
      contentType = InstructionContentTypes.ENABLE_DEVELOPER_MODE,
  ) {
    ThisInstruction(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
    ) {
      EnableDeveloperSettings()
    }
  }

  item(
      contentType = InstructionContentTypes.ACCEPT_PROMPT,
  ) {
    ThisInstruction(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
    ) {
      Column {
        Text(
            text = "You may be asked to accept a prompt to allow USB connections to your device.",
            style =
                MaterialTheme.typography.caption.copy(
                    color =
                        MaterialTheme.colors.onBackground.copy(
                            alpha = ContentAlpha.medium,
                        ),
                ),
        )

        Text(
            text = "Connect to your Laptop or Desktop with a USB cable",
            style = MaterialTheme.typography.body1,
        )
      }
    }
  }

  item(
      contentType = InstructionContentTypes.GRANT_PERMISSION,
  ) {
    OtherInstruction(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
    ) {
      Column {
        Text(
            text = "Grant $appName permission to change system battery settings using ADB.",
            style =
                MaterialTheme.typography.caption.copy(
                    color =
                        MaterialTheme.colors.onBackground.copy(
                            alpha = ContentAlpha.medium,
                        ),
                ),
        )
        Text(
            text = "Open a Terminal and execute the following command",
            style = MaterialTheme.typography.body1,
        )
      }
    }
  }

  item(
      contentType = InstructionContentTypes.ADB_COPY_COMMAND,
  ) {
    val hapticManager = LocalHapticManager.current
    val command = rememberAdbCommand(adbPermission)

    Column(
        modifier = itemModifier.padding(top = MaterialTheme.keylines.content),
    ) {
      Card(
          shape = MaterialTheme.shapes.medium,
          elevation = CardDefaults.Elevation,
          backgroundColor = Color.DarkGray,
      ) {
        SelectionContainer(
            modifier =
                Modifier.clickable {
                      hapticManager?.actionButtonPress()
                      onCopy(command)
                    }
                    .padding(MaterialTheme.keylines.content),
        ) {
          Text(
              text = command,
              style =
                  MaterialTheme.typography.body2.copy(
                      color = Color.Green,
                      fontWeight = FontWeight.W700,
                      fontFamily = FontFamily.Monospace,
                  ),
          )
        }
      }

      Box(
          modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content),
          contentAlignment = Alignment.Center,
      ) {
        Button(
            onClick = {
              hapticManager?.actionButtonPress()
              onCopy(command)
            },
        ) {
          Text(
              text = "Copy to Clipboard",
          )
        }
      }
    }
  }

  item(
      contentType = InstructionContentTypes.RESTART_APP,
  ) {
    val hapticManager = LocalHapticManager.current

    Column(
        modifier = itemModifier,
    ) {
      ThisInstruction(
          modifier = Modifier.padding(top = MaterialTheme.keylines.content),
      ) {
        Text(
            text = "Restart this application by clicking the button below, and re-opening it.",
            style = MaterialTheme.typography.body1,
        )
      }

      Box(
          modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content),
          contentAlignment = Alignment.Center,
      ) {
        Button(
            onClick = {
              hapticManager?.actionButtonPress()
              onRestartApp()
            },
        ) {
          Text(
              text = "Kill Application",
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun PreviewHomeSetupInstructions() {
  LazyColumn {
    renderSetupInstructions(
        appName = "TEST",
        onCopy = {},
        onRestartApp = {},
        adbPermission = android.Manifest.permission.WRITE_SECURE_SETTINGS,
    )
  }
}
