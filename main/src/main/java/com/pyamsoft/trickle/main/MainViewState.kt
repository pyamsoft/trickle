package com.pyamsoft.trickle.main

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.trickle.core.ActivityScope
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface MainViewState : UiViewState {
  val theme: StateFlow<Theming.Mode>
  val isSettingsOpen: StateFlow<Boolean>
}

@Stable
@ActivityScope
class MutableMainViewState @Inject internal constructor() : MainViewState {
  override val theme = MutableStateFlow(Theming.Mode.SYSTEM)
  override val isSettingsOpen = MutableStateFlow(false)
}
