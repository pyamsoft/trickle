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

import androidx.annotation.CheckResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming

@Composable
@CheckResult
private fun themeColors(isDarkMode: Boolean): Colors {
  val primary = colorResource(R.color.purple_500)
  val primaryVariant = colorResource(R.color.purple_700)
  val onPrimary = colorResource(R.color.white)
  val secondary = colorResource(R.color.teal_200)
  val secondaryVariant = colorResource(R.color.teal_700)
  val onSecondary = colorResource(R.color.white)

  return if (isDarkMode)
      darkColors(
          primary = primary,
          primaryVariant = primaryVariant,
          onPrimary = onPrimary,
          secondary = secondary,
          secondaryVariant = secondaryVariant,
          onSecondary = onSecondary,
      )
  else
      lightColors(
          primary = primary,
          primaryVariant = primaryVariant,
          onPrimary = onPrimary,
          secondary = secondary,
          secondaryVariant = secondaryVariant,
          onSecondary = onSecondary,
      )
}

@Composable
@CheckResult
private fun themeShapes(): Shapes {
  return Shapes(
      medium = RoundedCornerShape(4.dp),
  )
}

@Composable
fun TrickleTheme(
    themeProvider: ThemeProvider,
    content: @Composable () -> Unit,
) {
  TrickleTheme(
      theme = if (themeProvider.isDarkTheme()) Theming.Mode.DARK else Theming.Mode.LIGHT,
      content = content,
  )
}

@Composable
fun TrickleTheme(
    theme: Theming.Mode,
    content: @Composable () -> Unit,
) {
  val isDarkMode =
      when (theme) {
        Theming.Mode.LIGHT -> false
        Theming.Mode.DARK -> true
        Theming.Mode.SYSTEM -> isSystemInDarkTheme()
      }

  MaterialTheme(
      colors = themeColors(isDarkMode),
      shapes = themeShapes(),
  ) {
    // We update the LocalContentColor to match our onBackground. This allows the default
    // content color to be more appropriate to the theme background
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colors.onBackground,
        content = content,
    )
  }
}
