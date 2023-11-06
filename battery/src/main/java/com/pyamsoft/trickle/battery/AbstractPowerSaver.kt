package com.pyamsoft.trickle.battery

import androidx.annotation.CheckResult
import com.pyamsoft.trickle.battery.charging.BatteryCharge
import com.pyamsoft.trickle.core.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

internal abstract class AbstractPowerSaver
protected constructor(
    private val charger: BatteryCharge,
) : PowerSaver {

  private suspend fun changeSystemPowerSaving(
      force: Boolean,
      enable: Boolean,
  ): PowerSaver.State {
    if (!hasPermission()) {
      Timber.w { "Cannot change without permission" }
      return powerSavingError("CHANGE: Missing permission")
    }

    if (!isEnabled()) {
      Timber.w { "Cannot change when preference disabled" }
      return powerSavingError("CHANGE: Preference is disabled.")
    }

    // Check charging status first, we may not do anything
    val chargeStatus = charger.isCharging()
    if (chargeStatus == BatteryCharge.State.UNKNOWN) {
      Timber.w { "Battery Charge state is UNKNOWN, do not act" }
      return powerSavingError("CHANGE: Battery Charge Status is UNKNOWN.")
    }
    val isCharging = chargeStatus == BatteryCharge.State.CHARGING

    return if (enable) {
      attemptTurnOnSaver(
          isCharging = isCharging,
      )
    } else {
      attemptTurnOffSaver(
          force = force,
          isCharging = isCharging,
      )
    }
  }

  @CheckResult
  protected fun powerSavingError(message: String): PowerSaver.State {
    return PowerSaver.State.Failure(RuntimeException(message))
  }

  final override suspend fun setSystemPowerSaving(enable: Boolean): PowerSaver.State =
      withContext(context = Dispatchers.Default) {
        // Since this is dealing with a Android OS system state, we ensure this operation can
        // never be cancelled until it is completed
        val state =
            withContext(context = NonCancellable) {
              changeSystemPowerSaving(
                  force = false,
                  enable = enable,
              )
            }

        return@withContext state
      }

  final override suspend fun resetSystemPowerSavingState(): Boolean =
      withContext(context = Dispatchers.Default) {
        // Since this is dealing with a Android OS system state, we ensure this operation can
        // never be cancelled until it is completed
        val state =
            withContext(context = NonCancellable) {
              changeSystemPowerSaving(
                  force = true,
                  enable = false,
              )
            }
        return@withContext state == PowerSaver.State.Disabled
      }

  @CheckResult protected abstract fun hasPermission(): Boolean

  @CheckResult protected abstract suspend fun isEnabled(): Boolean

  @CheckResult
  protected abstract suspend fun attemptTurnOnSaver(isCharging: Boolean): PowerSaver.State

  @CheckResult
  protected abstract suspend fun attemptTurnOffSaver(
      force: Boolean,
      isCharging: Boolean,
  ): PowerSaver.State
}
