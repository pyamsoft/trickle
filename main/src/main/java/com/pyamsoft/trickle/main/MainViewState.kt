package com.pyamsoft.trickle.main

import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiViewState
import com.pyamsoft.trickle.core.ActivityScope
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface MainViewState : UiViewState {
  val permission: StateFlow<PermissionState>
  val isSettingsOpen: StateFlow<Boolean>

  enum class PermissionState {
    NONE,
    NOT_GRANTED,
    GRANTED
  }
}

@Stable
@ActivityScope
class MutableMainViewState @Inject constructor() : MainViewState {
  override val permission = MutableStateFlow(MainViewState.PermissionState.NONE)
  override val isSettingsOpen = MutableStateFlow(false)
}
