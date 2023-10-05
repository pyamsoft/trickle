package com.pyamsoft.trickle.battery

import androidx.annotation.CheckResult

interface PowerSaver {

  @CheckResult suspend fun setSystemPowerSaving(enable: Boolean): State

  @CheckResult suspend fun resetSystemPowerSavingState(): Boolean

  sealed interface State {
    data object Enabled : State

    data object Disabled : State

    data class Failure(val throwable: Throwable) : State
  }
}
