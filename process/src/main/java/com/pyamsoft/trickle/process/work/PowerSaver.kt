package com.pyamsoft.trickle.process.work

import androidx.annotation.CheckResult

interface PowerSaver {

  @CheckResult suspend fun attemptPowerSaving(enable: Boolean): Boolean
}
