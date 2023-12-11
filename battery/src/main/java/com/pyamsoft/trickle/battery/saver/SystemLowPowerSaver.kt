package com.pyamsoft.trickle.battery.saver

import android.content.Context
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.annotation.RequiresPermission
import com.pyamsoft.trickle.battery.PowerPreferences
import com.pyamsoft.trickle.battery.charging.BatteryCharge
import com.pyamsoft.trickle.battery.optimize.BatteryOptimizer
import com.pyamsoft.trickle.battery.permission.PermissionGuard
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

@Singleton
internal class SystemLowPowerSaver
@Inject
internal constructor(
    private val context: Context,
    private val preferences: PowerPreferences,
    private val permissions: PermissionGuard,
    private val optimizer: BatteryOptimizer,
    charger: BatteryCharge,
) :
    AbstractPowerSaver(
        charger = charger,
    ) {

  private val shouldTogglePowerSaving = MutableStateFlow(false)
  private val resolver by lazy { context.contentResolver }

  /** This should work if we have WRITE_SECURE_SETTINGS */
  @CheckResult
  @RequiresPermission(value = android.Manifest.permission.WRITE_SECURE_SETTINGS)
  private fun togglePowerSaving(enable: Boolean): PowerSaver.State {
    val value = if (enable) 1 else 0

    return try {
      Timber.d { "$name: Settings.Global.low_power=${value}" }
      if (Settings.Global.putInt(resolver, "low_power", value)) {
        if (enable) PowerSaver.State.Enabled else PowerSaver.State.Disabled
      } else {
        Timber.w { "$name: Failed to write Settings.Global.low_power $value" }
        powerSavingError("$name: Failed to write Settings.Global.low_power")
      }
    } catch (e: Throwable) {
      Timber.e(e) { "$name: Error writing settings global low_power $value" }
      PowerSaver.State.Failure(e)
    }
  }

  @CheckResult
  private fun decideToAct(force: Boolean, isCharging: Boolean): Boolean {
    if (force) {
      Timber.w { "$name DISABLE: Force Power Saving OFF" }
      return true
    }

    if (isCharging) {
      Timber.d { "$name DISABLE: Always turn OFF power saving when device is charging" }
      return true
    }

    return false
  }

  override fun hasPermission(): Boolean {
    return permissions.canWriteSystemSettings()
  }

  override suspend fun isEnabled(): Boolean {
    return preferences.observePowerSavingEnabled().first()
  }

  override suspend fun saveOn(isCharging: Boolean): PowerSaver.State {
    val isBeingOptimized = optimizer.isInPowerSavingMode()
    // Always set the "owner" flag back to false
    // We don't care about what any previous states were, we are only about the "last" screen
    // off before the first screen on.
    shouldTogglePowerSaving.value = false

    if (isCharging) {
      Timber.w { "$name ENABLE: Cannot turn power saving ON when device is CHARGING" }
      return powerSavingError("$name ENABLE: Device is Charging, do not turn ON.")
    }

    if (isBeingOptimized) {
      Timber.w { "$name ENABLE: Cannot turn power saving ON when device is already POWER_SAVING" }
      return powerSavingError("$name ENABLE: Device is power_saving, do not turn ON")
    }

    // If we pass all criteria, then we own the POWER_SAVING system status
    return if (shouldTogglePowerSaving.compareAndSet(expect = false, update = true)) {
      togglePowerSaving(enable = true)
    } else {
      powerSavingError("$name ENABLE: We have already set the toggle flag!")
    }
  }

  override suspend fun saveOff(
      force: Boolean,
      isCharging: Boolean,
  ): PowerSaver.State {
    // Only act if we own the POWER_SAVING status
    var act = shouldTogglePowerSaving.compareAndSet(expect = true, update = false)

    // If we were not flagged, but other special conditions exist
    if (!act) {
      if (decideToAct(
          force = force,
          isCharging = isCharging,
      )) {
        act = true
      }
    }

    return if (act) {
      togglePowerSaving(enable = false)
    } else {
      powerSavingError("$name DISABLE: We are not managing power, cannot act")
    }
  }

  override val name = "LOW_POWER_MODE"
}
