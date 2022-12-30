package com.pyamsoft.trickle.process

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

interface PowerPreferences {

  suspend fun setPowerSavingEnabled(enable: Boolean)

  @CheckResult suspend fun observePowerSavingEnabled(): Flow<Boolean>

  suspend fun setIgnoreInPowerSavingMode(ignore: Boolean)

  @CheckResult suspend fun observeIgnoreInPowerSavingMode(): Flow<Boolean>
}
