package com.pyamsoft.trickle.battery.optimize

import androidx.annotation.CheckResult

interface BatteryOptimizer {

  @CheckResult suspend fun isOptimizationsIgnored(): Boolean

  @CheckResult suspend fun isInPowerSavingMode(): Boolean
}
