package com.pyamsoft.trickle.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

@Suppress("unused")
val Icons.Filled.LocalCafe: ImageVector
  get() {
    if (_localCafe != null) {
      return _localCafe!!
    }
    _localCafe =
        materialIcon(name = "Filled.LocalCafe") {
          materialPath {
            moveTo(20.0f, 3.0f)
            lineTo(4.0f, 3.0f)
            verticalLineToRelative(10.0f)
            curveToRelative(0.0f, 2.21f, 1.79f, 4.0f, 4.0f, 4.0f)
            horizontalLineToRelative(6.0f)
            curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
            verticalLineToRelative(-3.0f)
            horizontalLineToRelative(2.0f)
            curveToRelative(1.11f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            lineTo(22.0f, 5.0f)
            curveToRelative(0.0f, -1.11f, -0.89f, -2.0f, -2.0f, -2.0f)
            close()
            moveTo(20.0f, 8.0f)
            horizontalLineToRelative(-2.0f)
            lineTo(18.0f, 5.0f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(3.0f)
            close()
            moveTo(4.0f, 19.0f)
            horizontalLineToRelative(16.0f)
            verticalLineToRelative(2.0f)
            lineTo(4.0f, 21.0f)
            close()
          }
        }
    return _localCafe!!
  }

@Suppress("ObjectPropertyName") private var _localCafe: ImageVector? = null
