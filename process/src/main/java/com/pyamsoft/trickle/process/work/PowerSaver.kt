package com.pyamsoft.trickle.process.work

import androidx.annotation.CheckResult

interface PowerSaver {

  @CheckResult suspend fun forcePowerSaveModeOn(): State

  @CheckResult suspend fun powerSaveModeOn(): State

  @CheckResult suspend fun forcePowerSaveModeOff(): State

  @CheckResult suspend fun powerSaveModeOff(): State

  sealed class State {
    object Enabled : State()
    object Disabled : State()
    data class Failure(val throwable: Throwable) : State()
  }
}
