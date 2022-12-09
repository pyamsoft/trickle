package com.pyamsoft.trickle.process.work

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.requireNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BatteryChargeImpl
@Inject
internal constructor(
    private val context: Context,
) : BatteryCharge {

  private val batteryManager by
      lazy(LazyThreadSafetyMode.NONE) {
        context.getSystemService<BatteryManager>().requireNotNull()
      }

  override suspend fun isCharging(): Boolean {
    return isBatteryChargingBattery() || isBatteryChargingIntent()
  }

  @CheckResult
  private fun isBatteryChargingIntent(): Boolean {
    val statusIntent: Intent? =
        ContextCompat.registerReceiver(
            context,
            null,
            BATTERY_STATUS_INTENT_FILTER,
            ContextCompat.RECEIVER_EXPORTED,
        )

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

  companion object {

    private val BATTERY_STATUS_INTENT_FILTER = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
  }
}
