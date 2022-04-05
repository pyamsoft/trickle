package com.pyamsoft.trickle.process.work

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.trickle.process.PowerPreferences
import com.pyamsoft.trickle.process.permission.PermissionChecker
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
internal class PowerSaverImpl
@Inject
internal constructor(
    private val context: Context,
    private val preferences: PowerPreferences,
    private val permissions: PermissionChecker,
) : PowerSaver {

  private val batteryManager by
      lazy(LazyThreadSafetyMode.NONE) {
        context.getSystemService<BatteryManager>().requireNotNull()
      }

  private val powerManager by
      lazy(LazyThreadSafetyMode.NONE) { context.getSystemService<PowerManager>().requireNotNull() }

  private val resolver by lazy(LazyThreadSafetyMode.NONE) { context.contentResolver }

  /**
   * When going down for power saving, we set this variable When coming back up from power saving we
   * read and unset this variable
   */
  private var ignorePowerWhenAlreadyInPowerSavingMode = false

  /** This should work if we have WRITE_SECURE_SETTINGS */
  @CheckResult
  private fun togglePowerSaving(enable: Boolean): PowerSaver.State {
    val value = if (enable) 1 else 0

    return try {
      if (Settings.Global.putInt(resolver, "low_power", value)) {
        if (enable) PowerSaver.State.Enabled else PowerSaver.State.Disabled
      } else {
        Timber.w("Failed to putInt into settings global low_power $value")
        powerSavingError("Failed to write global settings")
      }
    } catch (e: Throwable) {
      Timber.e(e, "Error writing settings global low_power $value")
      PowerSaver.State.Failure(e)
    }
  }

  private fun resetRunContext() {
    ignorePowerWhenAlreadyInPowerSavingMode = false
  }

  @CheckResult
  private fun isBatteryChargingIntent(): Boolean {
    val statusIntent: Intent? = context.registerReceiver(null, BATTERY_STATUS_INTENT_FILTER)
    val batteryStatus =
        statusIntent?.getIntExtra(
            BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
            ?: BatteryManager.BATTERY_STATUS_UNKNOWN
    return batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
        batteryStatus == BatteryManager.BATTERY_STATUS_FULL
  }

  @CheckResult
  private fun isBatteryChargingBattery(): Boolean {
    return batteryManager.isCharging
  }

  @CheckResult
  private fun isBatteryCharging(): Boolean {
    return isBatteryChargingBattery() || isBatteryChargingIntent()
  }

  @CheckResult
  private suspend fun turnPowerSavingOff(
      force: Boolean,
  ): PowerSaver.State {
    // If forced, we don't need to check previous power saving state or preference
    if (!force) {
      // Retrieve a previously written value
      val shouldIgnore = ignorePowerWhenAlreadyInPowerSavingMode

      // Reset running environment
      resetRunContext()

      // Check preference
      if (!preferences.isPowerSavingEnabled()) {
        Timber.w("Cannot turn power saving OFF when preference disabled")
        resetRunContext()
        return powerSavingError("Preference is disabled, cannot act")
      }

      // Check previous state
      if (shouldIgnore) {
        Timber.d("Power Saving was enabled from outside, do not turn OFF")
        return powerSavingError("Managed from outside, cannot act")
      }
    }

    Timber.d("Disable power saving mode!")
    return togglePowerSaving(enable = false)
  }

  @CheckResult
  private suspend fun turnPowerSavingOn(
      force: Boolean,
  ): PowerSaver.State {
    // Reset run environment
    resetRunContext()

    // If force, we don't need to check preference or current device power state
    if (!force) {
      if (!preferences.isPowerSavingEnabled()) {
        Timber.w("Cannot turn power saving ON when preference disabled")
        return powerSavingError("Preference is disabled, cannot act")
      }

      // Check charging status first, we may not do anything
      if (isBatteryCharging()) {
        Timber.w("Do not turn power saving ON while device charging")
        return powerSavingError("Device is charging, cannot act")
      }

      // Mark flag as false by default
      ignorePowerWhenAlreadyInPowerSavingMode = false

      // But if we are not charging, check if we are already in power-saving mode from outside
      // control, since if so, we do not want to override the device state
      if (preferences.isIgnoreInPowerSavingMode()) {
        if (powerManager.isPowerSaveMode) {
          // Mark this for later, see turnPowerSavingOff
          ignorePowerWhenAlreadyInPowerSavingMode = true

          Timber.d("Not enabling power-saving because we are in power-saving from outside!")
          return powerSavingError("Managed from outside, cannot act")
        }
      }
    }

    Timber.d("Enable power saving mode!")
    return togglePowerSaving(enable = true)
  }

  @CheckResult
  private suspend fun performPowerSaving(enable: Boolean, force: Boolean): PowerSaver.State =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()

        return@withContext coroutineScope {
          if (!permissions.hasSecureSettingsPermission()) {
            Timber.w("No power related work without WRITE_SECURE_SETTINGS permission")
            resetRunContext()
            return@coroutineScope powerSavingError("Missing WRITE_SECURE_SETTINGS permission")
          }

          // Check for this unique instance
          if (!force) {
            if (preferences.isExitPowerSavingModeWhileCharging()) {
              if (isBatteryCharging()) {
                Timber.d("Exit power saving mode unconditionally while device is charging")
                resetRunContext()
                return@coroutineScope togglePowerSaving(enable = false)
              }
            }
          }

          return@coroutineScope if (enable) {
            turnPowerSavingOn(force)
          } else {
            turnPowerSavingOff(force)
          }
        }
      }

  override suspend fun attemptPowerSaving(enable: Boolean): PowerSaver.State {
    return performPowerSaving(enable = enable, force = false)
  }

  override suspend fun forcePowerSaving(enable: Boolean): PowerSaver.State {
    return performPowerSaving(enable = enable, force = true)
  }

  companion object {

    private val BATTERY_STATUS_INTENT_FILTER = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    @JvmStatic
    @CheckResult
    private fun powerSavingError(message: String): PowerSaver.State {
      return PowerSaver.State.Failure(RuntimeException(message))
    }
  }
}
