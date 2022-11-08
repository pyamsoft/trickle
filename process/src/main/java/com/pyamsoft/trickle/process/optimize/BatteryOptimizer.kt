package com.pyamsoft.trickle.process.optimize

import androidx.annotation.CheckResult

interface BatteryOptimizer {

  @CheckResult suspend fun isOptimizationsIgnored(): Boolean
}
