package com.pyamsoft.trickle.process

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.util.PreferenceListener

interface PowerPreferences {

  @CheckResult suspend fun isPowerSavingEnabled(): Boolean

  suspend fun setPowerSavingEnabled(enable: Boolean)

  @CheckResult fun observerPowerSavingEnabled(onChange: (Boolean) -> Unit): PreferenceListener
}
