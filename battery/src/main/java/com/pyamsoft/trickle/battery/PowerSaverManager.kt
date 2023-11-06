package com.pyamsoft.trickle.battery

import androidx.annotation.CheckResult

interface PowerSaverManager {

  suspend fun savePower(enable: Boolean)

  @CheckResult suspend fun reset(): Boolean
}
