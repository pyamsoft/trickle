package com.pyamsoft.trickle.process

import androidx.annotation.CheckResult

interface PowerPreferences {

  @CheckResult suspend fun isPowerSavingEnabled(): Boolean

  suspend fun setPowerSavingEnabled(enable: Boolean)
}
