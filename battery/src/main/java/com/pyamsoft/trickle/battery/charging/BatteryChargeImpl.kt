package com.pyamsoft.trickle.battery.charging

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BatteryChargeImpl
@Inject
internal constructor(
    private val context: Context,
) : BatteryCharge {

  /**
   * We use the Intent instead of asking the BatteryManager since battery manager only reports
   * CHARGING when the device is actually going up
   *
   * A plugged in device is what we care about, even if it's full or charging
   */
  @CheckResult
  override suspend fun isCharging(): BatteryCharge.State {
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
    if (batteryStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
      // We have no idea
      return BatteryCharge.State.UNKNOWN
    }

    val isPluggedIn =
        batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
            batteryStatus == BatteryManager.BATTERY_STATUS_FULL
    return if (isPluggedIn) BatteryCharge.State.CHARGING else BatteryCharge.State.NOT_CHARGING
  }

  companion object {

    private val BATTERY_STATUS_INTENT_FILTER = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
  }
}
