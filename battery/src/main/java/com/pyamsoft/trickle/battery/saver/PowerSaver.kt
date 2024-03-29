package com.pyamsoft.trickle.battery.saver

import androidx.annotation.CheckResult

interface PowerSaver {

  @get:CheckResult val name: String

  @CheckResult suspend fun savePower(enable: Boolean): State

  @CheckResult suspend fun reset(): Boolean

  sealed interface State {
    data object Enabled : State

    data object Disabled : State

    data class NoOp(val reason: String) : State

    data class Failure(val throwable: Throwable) : State
  }
}
