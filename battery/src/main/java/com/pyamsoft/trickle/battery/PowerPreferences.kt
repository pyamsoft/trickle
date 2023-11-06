package com.pyamsoft.trickle.battery

import androidx.annotation.CheckResult
import kotlinx.coroutines.flow.Flow

interface PowerPreferences {

  fun togglePowerSavingEnabled()

  fun setPowerSavingEnabled(enable: Boolean)

  @CheckResult fun observePowerSavingEnabled(): Flow<Boolean>

  fun setForceDozeEnabled(enable: Boolean)

  @CheckResult fun observeForceDozeEnabled(): Flow<Boolean>
}
