package com.pyamsoft.trickle.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.arch.UiViewState
import javax.inject.Inject

interface HomeViewState : UiViewState {
  val loading: Boolean
  val isPowerSaving: Boolean
  val isIgnoreInPowerSavingMode: Boolean
  val hasPermission: Boolean
}

internal class MutableHomeViewState @Inject internal constructor() : HomeViewState {
  override var isPowerSaving by mutableStateOf(false)
  override var isIgnoreInPowerSavingMode by mutableStateOf(false)
  override var hasPermission by mutableStateOf(false)
  override var loading by mutableStateOf(false)
}
