package com.pyamsoft.trickle.battery.charging

import androidx.annotation.CheckResult

interface BatteryCharge {

  @CheckResult suspend fun isCharging(): State

  enum class State {
    UNKNOWN,
    CHARGING,
    NOT_CHARGING
  }
}
