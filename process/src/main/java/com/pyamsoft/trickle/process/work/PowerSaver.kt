package com.pyamsoft.trickle.process.work

import androidx.annotation.CheckResult

interface PowerSaver {

  @CheckResult suspend fun forcePowerSaving(enable: Boolean): State

  @CheckResult suspend fun attemptPowerSaving(enable: Boolean): State

  sealed class State {
    object Enabled : State()
    object Disabled : State()
    data class Failure(val throwable: Throwable) : State()
  }
}
