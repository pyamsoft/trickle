package com.pyamsoft.trickle.battery

import androidx.annotation.CheckResult

interface PowerSaverManager {

  suspend fun runPowerSavers(enable: Boolean)

  @CheckResult suspend fun resetSystemPowerSavingState(): Boolean
}
