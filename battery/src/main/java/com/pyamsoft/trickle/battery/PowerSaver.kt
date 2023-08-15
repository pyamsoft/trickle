package com.pyamsoft.trickle.battery

import androidx.annotation.CheckResult

interface PowerSaver {

  @CheckResult suspend fun setSystemPowerSaving(enable: Boolean): State

  @CheckResult suspend fun resetSystemPowerSavingState(): Boolean

  sealed interface State {
    object Enabled : State

    object Disabled : State

    data class Failure(val throwable: Throwable) : State
  }
}
