package com.pyamsoft.trickle.battery.doze

import android.content.Context
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.annotation.RequiresPermission
import com.pyamsoft.trickle.battery.AbstractPowerSaver
import com.pyamsoft.trickle.battery.PowerPreferences
import com.pyamsoft.trickle.battery.PowerSaver
import com.pyamsoft.trickle.battery.charging.BatteryCharge
import com.pyamsoft.trickle.battery.permission.PermissionGuard
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

private const val DOZE_ON =
    "inactive_to=600000,light_after_inactive_to=300000,idle_after_inactive_to=5100,sensing_to=5100,locating_to=5100,location_accuracy=10000"

@Singleton
internal class ForceDozeImpl
@Inject
internal constructor(
    private val context: Context,
    private val preferences: PowerPreferences,
    private val permissions: PermissionGuard,
    charger: BatteryCharge,
) :
    AbstractPowerSaver(
        charger = charger,
    ) {

  private val shouldToggleDoze = MutableStateFlow(false)
  private val resolver by lazy { context.contentResolver }

  /** This should work if we have WRITE_SECURE_SETTINGS */
  @CheckResult
  @RequiresPermission(value = android.Manifest.permission.WRITE_SECURE_SETTINGS)
  private fun toggleForceDoze(enable: Boolean): PowerSaver.State {
    val value = if (enable) DOZE_ON else null
    return try {
      Timber.d { "DOZE: Settings.Global.device_idl_constants=${value}" }
      if (Settings.Global.putString(resolver, "device_idle_constants", value)) {
        if (enable) PowerSaver.State.Enabled else PowerSaver.State.Disabled
      } else {
        Timber.w { "DOZE: Failed to write Settings.Global.device_idle_constants $value" }
        powerSavingError("POWER_SAVING: Failed to write Settings.Global.device_idle_constants")
      }
    } catch (e: Throwable) {
      Timber.e(e) { "DOZE: Error writing settings global device_idle_constants $value" }
      PowerSaver.State.Failure(e)
    }
  }

  override suspend fun isEnabled(): Boolean {
    return preferences.observeForceDozeEnabled().first()
  }

  override fun hasPermission(): Boolean {
    return permissions.canWriteSystemSettings()
  }

  override suspend fun attemptTurnOnSaver(
      isCharging: Boolean,
  ): PowerSaver.State {
    // Always set the "owner" flag back to false
    // We don't care about what any previous states were, we are only about the "last" screen
    // off before the first screen on.
    shouldToggleDoze.value = false

    if (isCharging) {
      Timber.w { "DOZE ENABLE: Cannot turn doze ON when device is CHARGING" }
      return powerSavingError("DOZE ENABLE: Device is Charging, do not turn ON.")
    }

    // If we pass all criteria, then we own the POWER_SAVING system status
    return if (shouldToggleDoze.compareAndSet(expect = false, update = true)) {
      toggleForceDoze(enable = true)
    } else {
      powerSavingError("DOZE ENABLE: We have already set the toggle flag!")
    }
  }

  override suspend fun attemptTurnOffSaver(
      force: Boolean,
      isCharging: Boolean,
  ): PowerSaver.State {
    // Only act if we own the POWER_SAVING status
    var act = shouldToggleDoze.compareAndSet(expect = true, update = false)

    // If we were not flagged, but other special conditions exist
    if (!act) {
      if (force) {
        Timber.w { "DOZE DISABLE: Force DOZE OFF" }
        act = true
      } else {
        if (isCharging) {
          Timber.d { "DOZE DISABLE: Always turn OFF DOZE when device is charging" }
          act = true
        }
      }
    }

    return if (act) {
      toggleForceDoze(enable = false)
    } else {
      powerSavingError("DOZE DISABLE: We are not managing power, cannot act")
    }
  }
}
