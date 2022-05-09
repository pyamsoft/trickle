package com.pyamsoft.trickle.process.work

import androidx.annotation.CheckResult

interface BatteryCharge {

  @CheckResult suspend fun isCharging(): Boolean
}
