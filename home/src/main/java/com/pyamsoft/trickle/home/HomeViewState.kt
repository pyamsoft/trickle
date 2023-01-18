package com.pyamsoft.trickle.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.pyamsoft.pydroid.arch.UiViewState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface HomeViewState : UiViewState {
  val loadingState: StateFlow<LoadingState>
  val permissionState: StateFlow<PermissionState>

  val isPowerSaving: StateFlow<Boolean>
  val isIgnoreInPowerSavingMode: StateFlow<Boolean>

  val isPowerSettingsShortcutVisible: StateFlow<Boolean>

  val isBatteryOptimizationsIgnored: StateFlow<Boolean>

  val isTroubleshooting: StateFlow<Boolean>

  @Stable
  @Immutable
  enum class LoadingState {
    NONE,
    LOADING,
    DONE
  }

  @Stable
  @Immutable
  enum class PermissionState {
    NONE,
    GRANTED,
    DENIED
  }
}

@Stable
class MutableHomeViewState @Inject internal constructor() : HomeViewState {

  override val loadingState = MutableStateFlow(HomeViewState.LoadingState.NONE)
  override val permissionState = MutableStateFlow(HomeViewState.PermissionState.NONE)

  override val isPowerSaving = MutableStateFlow(false)
  override val isIgnoreInPowerSavingMode = MutableStateFlow(false)

  override val isPowerSettingsShortcutVisible = MutableStateFlow(false)

  override val isBatteryOptimizationsIgnored = MutableStateFlow(false)

  override val isTroubleshooting = MutableStateFlow(false)
}
