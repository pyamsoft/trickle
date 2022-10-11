package com.pyamsoft.trickle.home

import android.content.Context
import androidx.annotation.CheckResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.trickle.ui.icons.Devices
import com.pyamsoft.trickle.ui.icons.PhoneAndroid

val THIS_DEVICE_COLOR = Color(0xFF4CAF50)
val OTHER_DEVICE_COLOR = Color(0xFF2196F3)

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
      tint = THIS_DEVICE_COLOR,
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
      contentDescription = "Other Devices",
      tint = OTHER_DEVICE_COLOR,
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
    ThisDevice(small = small)

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
    OtherDevice(small = small)

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
) {
  text
      .getStringAnnotations(
          tag = ADB_URI_TAG,
          start = start,
          end = start + ADB_LINK_TEXT.length,
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
      append("Download ")
      withStringAnnotation(
          tag = ADB_URI_TAG,
          annotation = ADB_URL,
      ) {
        withStyle(
            style =
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colors.primary,
                ),
        ) {
          append(ADB_LINK_TEXT)
        }
      }

      append(" and install it on your Laptop or Desktop machine.")
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(
        text = text,
        style = MaterialTheme.typography.body1,
        onClick = {
          onTextClicked(
              text = text,
              uriHandler = uriHandler,
              start = it,
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
      append("Enable ")
      withStringAnnotation(
          tag = DEV_SETTINGS_URI_TAG,
          annotation = DEV_SETTINGS_URL,
      ) {
        withStyle(
            style =
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colors.primary,
                ),
        ) {
          append(DEV_SETTINGS_LINK_TEXT)
        }
      }

      append(" on your device")
    }

    val uriHandler = LocalUriHandler.current
    ClickableText(
        text = text,
        style = MaterialTheme.typography.body1,
        onClick = {
          onTextClicked(
              text = text,
              uriHandler = uriHandler,
              start = it,
          )
        },
    )
  }
}

@CheckResult
private fun createAdbCommand(context: Context): String {
  return "adb shell pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS"
}

@Composable
@CheckResult
private fun rememberAdbCommand(): String {
  val context = LocalContext.current
  return remember(context) { createAdbCommand(context) }
}

internal fun LazyListScope.renderHomeSetupInstructions(
    appName: String,
    onCopy: (String) -> Unit,
    onRestartApp: () -> Unit,
) {
  item {
    Column(
        modifier = Modifier.padding(bottom = MaterialTheme.keylines.content),
    ) {
      ThisInstruction(
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

      OtherInstruction(
          small = true,
      ) {
        Text(
            text = "Other Device",
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
  }

  item {
    OtherInstruction(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      DownloadAdb()
    }
  }

  item {
    ThisInstruction(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      EnableDeveloperSettings()
    }
  }

  item {
    ThisInstruction(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
    ) {
      Text(
          text = "Connect to your Laptop or Desktop with a USB cable",
          style = MaterialTheme.typography.body1,
      )
    }
  }

  item {
    OtherInstruction(
        modifier = Modifier.padding(top = MaterialTheme.keylines.content),
    ) {
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

  item {
    val command = rememberAdbCommand()

    Column {
      SelectionContainer {
        Text(
            modifier =
                Modifier.clickable { onCopy(command) }
                    .padding(top = MaterialTheme.keylines.content)
                    .background(color = Color.DarkGray)
                    .padding(MaterialTheme.keylines.content),
            text = command,
            style =
                MaterialTheme.typography.body2.copy(
                    color = Color.Green,
                    fontWeight = FontWeight.W700,
                    fontFamily = FontFamily.Monospace,
                ),
        )
      }

      Box(
          modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content),
          contentAlignment = Alignment.Center,
      ) {
        Button(
            onClick = { onCopy(command) },
        ) {
          Text(
              text = "Copy to Clipboard",
          )
        }
      }
    }
  }

  item {
    Column {
      ThisInstruction(
          modifier = Modifier.padding(top = MaterialTheme.keylines.content),
      ) {
        Text(
            text =
                "Restart this application by clicking the button below, or by swiping this application out of your Recents screen.",
            style = MaterialTheme.typography.body1,
        )
      }

      Box(
          modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.keylines.content),
          contentAlignment = Alignment.Center,
      ) {
        Button(
            onClick = onRestartApp,
        ) {
          Text(
              text = "Kill Application",
          )
        }
      }
    }
  }
}
