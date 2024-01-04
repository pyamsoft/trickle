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

  val isPowerSaving: StateFlow<Boolean>
  val isBatteryOptimizationsIgnored: StateFlow<Boolean>

  val hasNotificationPermission: StateFlow<Boolean>

  val isPowerSettingsShortcutVisible: StateFlow<Boolean>
  val isTroubleshooting: StateFlow<Boolean>

  val isAlwaysForceBackground: StateFlow<Boolean>

  @Stable
  @Immutable
  enum class LoadingState {
    NONE,
    LOADING,
    DONE
  }
}

@Stable
class MutableHomeViewState @Inject internal constructor() : HomeViewState {
  override val loadingState = MutableStateFlow(HomeViewState.LoadingState.NONE)

  override val isPowerSaving = MutableStateFlow(false)
  override val isBatteryOptimizationsIgnored = MutableStateFlow(false)

  override val hasNotificationPermission = MutableStateFlow(false)

  override val isPowerSettingsShortcutVisible = MutableStateFlow(false)
  override val isTroubleshooting = MutableStateFlow(false)

  override val isAlwaysForceBackground = MutableStateFlow(false)
}
