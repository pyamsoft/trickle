package com.pyamsoft.trickle.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pyamsoft.pydroid.arch.UiViewState
import javax.inject.Inject

interface HomeViewState : UiViewState {
  val isPowerSaving: Boolean
  val hasPermission: Boolean
}

internal class MutableHomeViewState @Inject internal constructor() : HomeViewState {
  override var isPowerSaving by mutableStateOf(false)
  override var hasPermission by mutableStateOf(false)
}
