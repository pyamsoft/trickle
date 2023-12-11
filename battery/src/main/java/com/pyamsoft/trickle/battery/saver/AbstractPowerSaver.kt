package com.pyamsoft.trickle.battery.saver

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

  @CheckResult
  private suspend fun runPowerSaver(
      force: Boolean,
      enable: Boolean,
  ): PowerSaver.State {
    if (!hasPermission()) {
      Timber.w { "$name Cannot change without permission" }
      return powerSavingError("$name CHANGE: Missing permission")
    }

    if (!isEnabled()) {
      Timber.w { "$name Cannot change when preference disabled" }
      return powerSavingNoOp("$name CHANGE: Preference is disabled.")
    }

    // Check charging status first, we may not do anything
    val chargeStatus = charger.isCharging()
    if (chargeStatus == BatteryCharge.State.UNKNOWN) {
      Timber.w { "$name Battery Charge state is UNKNOWN, do not act" }
      return powerSavingError("$name CHANGE: Battery Charge Status is UNKNOWN.")
    }
    val isCharging = chargeStatus == BatteryCharge.State.CHARGING

    return if (enable) {
      saveOn(
          isCharging = isCharging,
      )
    } else {
      saveOff(
          force = force,
          isCharging = isCharging,
      )
    }
  }

  @CheckResult
  protected fun powerSavingError(message: String): PowerSaver.State {
    return PowerSaver.State.Failure(RuntimeException(message))
  }

  @CheckResult
  protected fun powerSavingNoOp(message: String): PowerSaver.State {
    return PowerSaver.State.NoOp(message)
  }

  final override suspend fun savePower(enable: Boolean): PowerSaver.State =
      withContext(context = Dispatchers.Default) {
        // Since this is dealing with a Android OS system state, we ensure this operation can
        // never be cancelled until it is completed
        val state =
            withContext(context = NonCancellable) {
              runPowerSaver(
                  force = false,
                  enable = enable,
              )
            }

        return@withContext state
      }

  final override suspend fun reset(): Boolean =
      withContext(context = Dispatchers.Default) {
        // Since this is dealing with a Android OS system state, we ensure this operation can
        // never be cancelled until it is completed
        val state =
            withContext(context = NonCancellable) {
              runPowerSaver(
                  force = true,
                  enable = false,
              )
            }
        return@withContext state == PowerSaver.State.Disabled
      }

  @CheckResult protected abstract fun hasPermission(): Boolean

  @CheckResult protected abstract suspend fun isEnabled(): Boolean

  @CheckResult protected abstract suspend fun saveOn(isCharging: Boolean): PowerSaver.State

  @CheckResult
  protected abstract suspend fun saveOff(
      force: Boolean,
      isCharging: Boolean,
  ): PowerSaver.State
}
