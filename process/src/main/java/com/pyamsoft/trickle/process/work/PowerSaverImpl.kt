package com.pyamsoft.trickle.process.work

import android.content.Context
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.annotation.RequiresPermission
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.trickle.process.PowerPreferences
import com.pyamsoft.trickle.process.permission.PermissionChecker
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
internal class PowerSaverImpl
@Inject
internal constructor(
    private val context: Context,
    private val preferences: PowerPreferences,
    private val permissions: PermissionChecker,
    private val batteryCharge: BatteryCharge,
) : PowerSaver {

  private val resolver by lazy(LazyThreadSafetyMode.NONE) { context.contentResolver }

  /** This should work if we have WRITE_SECURE_SETTINGS */
  @CheckResult
  @RequiresPermission(value = android.Manifest.permission.WRITE_SECURE_SETTINGS)
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

  @CheckResult
  private suspend fun isPowerSavingEnabled(): Boolean {
    return preferences.observePowerSavingEnabled().first()
  }

  @CheckResult
  private suspend fun turnPowerSavingOff(
      force: Boolean,
  ): PowerSaver.State {
    // If forced, we don't need to check previous power saving state or preference
    if (!force) {

      // Check preference
      if (!isPowerSavingEnabled()) {
        Timber.w("Cannot turn power saving OFF when preference disabled")
        return powerSavingError("Preference is disabled, cannot act")
      }
    }

    Timber.d("Disable power saving mode!")
    return togglePowerSaving(enable = false)
  }

  @CheckResult
  private suspend fun turnPowerSavingOn(
      force: Boolean,
  ): PowerSaver.State {
    // If force, we don't need to check preference or current device power state
    if (!force) {
      if (!isPowerSavingEnabled()) {
        Timber.w("Cannot turn power saving ON when preference disabled")
        return powerSavingError("Preference is disabled, cannot act")
      }

      // Check charging status first, we may not do anything
      if (batteryCharge.isCharging()) {
        Timber.w("Do not turn power saving ON while device charging")
        return powerSavingError("Device is charging, cannot act")
      }
    }

    Timber.d("Enable power saving mode!")
    return togglePowerSaving(enable = true)
  }

  @CheckResult
  private suspend inline fun performPowerSaving(
      force: Boolean,
      crossinline block: suspend (Boolean) -> PowerSaver.State
  ): PowerSaver.State =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()

        if (!permissions.hasSecureSettingsPermission()) {
          Timber.w("No power related work without WRITE_SECURE_SETTINGS permission")
          return@withContext powerSavingError("Missing WRITE_SECURE_SETTINGS permission")
        }

        return@withContext block(force)
      }

  override suspend fun powerSaveModeOff(): PowerSaver.State {
    return performPowerSaving(force = false) { turnPowerSavingOff(it) }
  }

  override suspend fun forcePowerSaveModeOff(): PowerSaver.State {
    return performPowerSaving(force = true) { turnPowerSavingOff(it) }
  }

  override suspend fun powerSaveModeOn(): PowerSaver.State {
    return performPowerSaving(force = false) { turnPowerSavingOn(it) }
  }

  override suspend fun forcePowerSaveModeOn(): PowerSaver.State {
    return performPowerSaving(force = true) { turnPowerSavingOn(it) }
  }

  companion object {

    @JvmStatic
    @CheckResult
    private fun powerSavingError(message: String): PowerSaver.State {
      return PowerSaver.State.Failure(RuntimeException(message))
    }
  }
}
