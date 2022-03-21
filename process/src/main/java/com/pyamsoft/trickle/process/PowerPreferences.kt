package com.pyamsoft.trickle.process

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.util.PreferenceListener

interface PowerPreferences {

  @CheckResult suspend fun isPowerSavingEnabled(): Boolean

  suspend fun setPowerSavingEnabled(enable: Boolean)

  @CheckResult fun observePowerSavingEnabled(onChange: (Boolean) -> Unit): PreferenceListener

  @CheckResult suspend fun isIgnoreInPowerSavingMode(): Boolean

  suspend fun setIgnoreInPowerSavingMode(ignore: Boolean)

  @CheckResult fun observeIgnoreInPowerSavingMode(onChange: (Boolean) -> Unit): PreferenceListener

  @CheckResult suspend fun isExitPowerSavingModeWhileCharging(): Boolean

  suspend fun setExitPowerSavingModeWhileCharging(exit: Boolean)

  @CheckResult
  fun observeExitPowerSavingModeWhileCharging(onChange: (Boolean) -> Unit): PreferenceListener
}
