package com.pyamsoft.trickle.process

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

interface PowerPreferences {

  @CheckResult suspend fun isPowerSavingEnabled(): Boolean

  suspend fun setPowerSavingEnabled(enable: Boolean)

  @CheckResult suspend fun observePowerSavingEnabled(): Flow<Boolean>

  @CheckResult suspend fun isIgnoreInPowerSavingMode(): Boolean

  suspend fun setIgnoreInPowerSavingMode(ignore: Boolean)

  @CheckResult suspend fun observeIgnoreInPowerSavingMode(): Flow<Boolean>

  @CheckResult suspend fun isExitPowerSavingModeWhileCharging(): Boolean

  suspend fun setExitPowerSavingModeWhileCharging(exit: Boolean)

  @CheckResult suspend fun observeExitPowerSavingModeWhileCharging(): Flow<Boolean>
}
