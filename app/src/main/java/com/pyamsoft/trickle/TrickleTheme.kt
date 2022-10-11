/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.trickle

import android.app.Activity
import androidx.annotation.CheckResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.material.R
import com.pyamsoft.pydroid.theme.PYDroidTheme
import com.pyamsoft.pydroid.theme.attributesFromCurrentTheme
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming

@Composable
@CheckResult
private fun themeColors(activity: Activity, isDarkMode: Boolean): Colors {
  val colors =
      remember(isDarkMode) {
        activity.attributesFromCurrentTheme(
            R.attr.colorPrimary,
            R.attr.colorOnPrimary,
            R.attr.colorPrimaryVariant,
            R.attr.colorSecondary,
            R.attr.colorOnSecondary,
            R.attr.colorSecondaryVariant,
        )
      }
  val primary = colorResource(colors[0])
  val onPrimary = colorResource(colors[1])
  val primaryVariant = colorResource(colors[2])
  val secondary = colorResource(colors[3])
  val onSecondary = colorResource(colors[4])
  val secondaryVariant = colorResource(colors[5])

  return if (isDarkMode)
      darkColors(
          primary = primary,
          onPrimary = onPrimary,
          secondary = secondary,
          onSecondary = onSecondary,
          // Must be specified for things like Switch color
          primaryVariant = primaryVariant,
          secondaryVariant = secondaryVariant,
      )
  else
      lightColors(
          primary = primary,
          onPrimary = onPrimary,
          secondary = secondary,
          onSecondary = onSecondary,
          // Must be specified for things like Switch color
          primaryVariant = primaryVariant,
          secondaryVariant = secondaryVariant,
      )
}

@Composable
@CheckResult
private fun themeShapes(): Shapes {
  return Shapes(
      // Don't use MaterialTheme.keylines here incase it is customized
      medium = RoundedCornerShape(16.dp),
  )
}

@Composable
@CheckResult
private fun themeTypography(): Typography {
  val typography = MaterialTheme.typography
  return typography.copy(
      h1 =
          typography.h1.copy(
              fontWeight = FontWeight.W400,
          ),
      h2 =
          typography.h2.copy(
              fontWeight = FontWeight.W400,
          ),
      h3 =
          typography.h3.copy(
              fontWeight = FontWeight.W400,
          ),
      h4 =
          typography.h4.copy(
              fontWeight = FontWeight.W400,
          ),
      h5 =
          typography.h5.copy(
              fontWeight = FontWeight.W400,
          ),
      h6 =
          typography.h6.copy(
              fontWeight = FontWeight.W400,
          ),
      subtitle1 =
          typography.subtitle1.copy(
              fontWeight = FontWeight.W400,
          ),
      subtitle2 =
          typography.subtitle2.copy(
              fontWeight = FontWeight.W400,
          ),
      body1 =
          typography.body1.copy(
              fontWeight = FontWeight.W400,
          ),
      body2 =
          typography.body2.copy(
              fontWeight = FontWeight.W400,
          ),
      caption =
          typography.caption.copy(
              fontWeight = FontWeight.W400,
          ),
      overline =
          typography.overline.copy(
              fontWeight = FontWeight.W400,
          ),
  )
}

@Composable
fun Activity.TrickleTheme(
    themeProvider: ThemeProvider,
    content: @Composable () -> Unit,
) {
  this.TrickleTheme(
      theme = if (themeProvider.isDarkTheme()) Theming.Mode.DARK else Theming.Mode.LIGHT,
      content = content,
  )
}

@Composable
fun Activity.TrickleTheme(
    theme: Theming.Mode,
    content: @Composable () -> Unit,
) {
  val isDarkMode =
      when (theme) {
        Theming.Mode.LIGHT -> false
        Theming.Mode.DARK -> true
        Theming.Mode.SYSTEM -> isSystemInDarkTheme()
      }

  PYDroidTheme(
      colors = themeColors(this, isDarkMode),
      shapes = themeShapes(),
      typography = themeTypography()) {
    // We update the LocalContentColor to match our onBackground. This allows the default
    // content color to be more appropriate to the theme background
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colors.onBackground,
        content = content,
    )
  }
}
