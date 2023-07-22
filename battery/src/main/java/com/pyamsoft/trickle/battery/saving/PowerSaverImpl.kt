package com.pyamsoft.trickle.battery.saving

import android.content.Context
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.annotation.RequiresPermission
import com.pyamsoft.trickle.battery.PowerPreferences
import com.pyamsoft.trickle.battery.PowerSaver
import com.pyamsoft.trickle.battery.charging.BatteryCharge
import com.pyamsoft.trickle.battery.optimize.BatteryOptimizer
import com.pyamsoft.trickle.battery.permission.PermissionGuard
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
internal class PowerSaverImpl
@Inject
internal constructor(
    private val context: Context,
    private val preferences: PowerPreferences,
    private val permissions: PermissionGuard,
    private val charger: BatteryCharge,
    private val optimizer: BatteryOptimizer,
) : PowerSaver {

  private val shouldTogglePowerSaving = MutableStateFlow(false)
  private val resolver by lazy { context.contentResolver }

  /** This should work if we have WRITE_SECURE_SETTINGS */
  @CheckResult
  @RequiresPermission(value = android.Manifest.permission.WRITE_SECURE_SETTINGS)
  private fun togglePowerSaving(enable: Boolean): PowerSaver.State {
    val value = if (enable) 1 else 0

    return try {
      Timber.d("POWER_SAVING: Settings.Global.low_power=${value}")
      if (Settings.Global.putInt(resolver, "low_power", value)) {
        if (enable) PowerSaver.State.Enabled else PowerSaver.State.Disabled
      } else {
        Timber.w("POWER_SAVING: Failed to write Settings.Global.low_power $value")
        powerSavingError("POWER_SAVING: Failed to write Settings.Global.low_power")
      }
    } catch (e: Throwable) {
      Timber.e(e, "Error writing settings global low_power $value")
      PowerSaver.State.Failure(e)
    }
  }

  @CheckResult
  private suspend fun isManageSystemPowerEnabled(): Boolean {
    return preferences.observePowerSavingEnabled().first()
  }

  @CheckResult
  private fun attemptTurnOnPowerSaving(
      isCharging: Boolean,
      isBeingOptimized: Boolean,
  ): PowerSaver.State {
    if (isCharging) {
      Timber.w("ENABLE: Cannot turn power saving ON when device is CHARGING")
      return powerSavingError("ENABLE: Device is Charging, do not turn ON.")
    }

    if (isBeingOptimized) {
      Timber.w("ENABLE: Cannot turn power saving ON when device is already POWER_SAVING")
      return powerSavingError("ENABLE: Device is power_saving, do not turn ON")
    }

    // If we pass all criteria, then we own the POWER_SAVING system status
    return if (shouldTogglePowerSaving.compareAndSet(expect = false, update = true)) {
      togglePowerSaving(enable = true)
    } else {
      powerSavingError("ENABLE: We have already set the toggle flag!")
    }
  }

  @CheckResult
  private fun attemptTurnOffPowerSaving(
      force: Boolean,
      isCharging: Boolean,
  ): PowerSaver.State {
    var act: Boolean
    if (force) {
      Timber.w("DISABLE: Force Power Saving OFF")
      act = true

      // Reset our internal flag
      shouldTogglePowerSaving.value = false
    } else {
      // Only act if we own the POWER_SAVING status
      act = shouldTogglePowerSaving.compareAndSet(expect = true, update = false)

      if (!act) {
        if (isCharging) {
          Timber.d("DISABLE: Always turn OFF power saving when device is charging")
          act = true
        }
      }
    }

    return if (act) {
      togglePowerSaving(enable = false)
    } else {
      powerSavingError("DISABLE: We are not managing power, cannot act")
    }
  }

  private suspend fun changeSystemPowerSaving(
      force: Boolean,
      enable: Boolean,
  ): PowerSaver.State {
    if (!permissions.canManageSystemPower()) {
      Timber.w("Cannot change power_saving without WRITE_SECURE_SETTINGS permission")
      return powerSavingError("CHANGE: Missing WRITE_SECURE_SETTINGS permission")
    }

    if (!isManageSystemPowerEnabled()) {
      Timber.w("Cannot change power_saving when preference disabled")
      return powerSavingError("CHANGE: Preference is disabled.")
    }

    // Check charging status first, we may not do anything
    val chargeStatus = charger.isCharging()
    if (chargeStatus == BatteryCharge.State.UNKNOWN) {
      Timber.w("Battery Charge state is UNKNOWN, do not act")
      return powerSavingError("CHANGE: Battery Charge Status is UNKNOWN.")
    }
    val isCharging = chargeStatus == BatteryCharge.State.CHARGING

    return if (enable) {
      val isBeingOptimized = optimizer.isInPowerSavingMode()
      Timber.d("Attempt to turn power_saving ON")
      attemptTurnOnPowerSaving(
          isCharging = isCharging,
          isBeingOptimized = isBeingOptimized,
      )
    } else {
      Timber.d("Attempt to turn power_saving OFF")
      attemptTurnOffPowerSaving(
          force = force,
          isCharging = isCharging,
      )
    }
  }

  override suspend fun setSystemPowerSaving(enable: Boolean): PowerSaver.State =
      withContext(
          // Since this is dealing with a Android OS system state, we ensure this operation can
          // never be cancelled until it is completed
          context = Dispatchers.Default + NonCancellable) {
            changeSystemPowerSaving(
                force = false,
                enable = enable,
            )
          }

  override suspend fun resetSystemPowerSavingState(): Boolean =
      withContext(
          // Since this is dealing with a Android OS system state, we ensure this operation can
          // never be cancelled until it is completed
          context = Dispatchers.Default + NonCancellable) {
            // Force power saving OFF
            val state =
                changeSystemPowerSaving(
                    force = true,
                    enable = false,
                )
            return@withContext state == PowerSaver.State.Disabled
          }

  companion object {

    @JvmStatic
    @CheckResult
    private fun powerSavingError(message: String): PowerSaver.State {
      return PowerSaver.State.Failure(RuntimeException(message))
    }
  }
}
