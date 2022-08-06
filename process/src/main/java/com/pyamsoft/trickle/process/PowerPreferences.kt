package com.pyamsoft.trickle.process

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

interface PowerPreferences {

  @CheckResult suspend fun isPowerSavingEnabled(): Boolean

  suspend fun setPowerSavingEnabled(enable: Boolean)

  @CheckResult fun observePowerSavingEnabled(): Flow<Boolean>

  @CheckResult suspend fun isIgnoreInPowerSavingMode(): Boolean

  suspend fun setIgnoreInPowerSavingMode(ignore: Boolean)

  @CheckResult fun observeIgnoreInPowerSavingMode(): Flow<Boolean>

  @CheckResult suspend fun isExitPowerSavingModeWhileCharging(): Boolean

  suspend fun setExitPowerSavingModeWhileCharging(exit: Boolean)

  @CheckResult fun observeExitPowerSavingModeWhileCharging(): Flow<Boolean>
}
