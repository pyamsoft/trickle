package com.pyamsoft.trickle.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.trickle.core.ActivityScope
import javax.inject.Inject

interface MainViewState : UiViewState {
  val theme: Theming.Mode
}

@ActivityScope
internal class MutableMainViewState @Inject internal constructor() : MainViewState {
  override var theme by mutableStateOf(Theming.Mode.SYSTEM)
}
