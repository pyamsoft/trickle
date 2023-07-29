package com.pyamsoft.trickle.battery.charging

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BatteryChargeImpl
@Inject
internal constructor(
    private val context: Context,
) : BatteryCharge {

  private val batteryManager by lazy { context.getSystemService<BatteryManager>().requireNotNull() }

  override suspend fun isCharging(): BatteryCharge.State {
    val managerState = isBatteryChargingBattery()

    // Sometimes the manager is inaccurate - fall back to the intent
    return if (managerState != BatteryCharge.State.CHARGING) {
      isBatteryChargingIntent()
    } else {
      managerState
    }
  }

  @CheckResult
  private fun isBatteryChargingIntent(): BatteryCharge.State {
    val statusIntent =
        ContextCompat.registerReceiver(
            context,
            null,
            BATTERY_STATUS_INTENT_FILTER,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

    if (statusIntent == null) {
      Timber.w { "Battery Status Intent was null - we don't know the charging state" }
      return BatteryCharge.State.UNKNOWN
    }

    val batteryStatus =
        statusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
    return if (batteryStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
      // We have no idea
      BatteryCharge.State.UNKNOWN
    } else {
      val isCharging =
          batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
              batteryStatus == BatteryManager.BATTERY_STATUS_FULL
      // We do know
      if (isCharging) BatteryCharge.State.CHARGING else BatteryCharge.State.NOT_CHARGING
    }
  }

  @CheckResult
  private fun isBatteryChargingBattery(): BatteryCharge.State {
    return if (batteryManager.isCharging) BatteryCharge.State.CHARGING
    else BatteryCharge.State.NOT_CHARGING
  }

  companion object {

    private val BATTERY_STATUS_INTENT_FILTER = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
  }
}
