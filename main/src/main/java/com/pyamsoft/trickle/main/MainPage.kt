package com.pyamsoft.trickle.main

import android.os.Bundle
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.navigator.Navigator

sealed class MainPage(val name: String) {
  object Home : MainPage("Home")

  @CheckResult
  fun asScreen(): Navigator.Screen<MainPage> {
    val self = this
    return object : Navigator.Screen<MainPage> {
      override val arguments: Bundle? = null
      override val screen: MainPage = self
    }
  }
}
