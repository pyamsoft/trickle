package com.pyamsoft.trickle.battery.saver

import androidx.annotation.CheckResult

interface PowerSaverManager {

  suspend fun savePower(enable: Boolean)

  @CheckResult suspend fun reset(): Boolean
}
