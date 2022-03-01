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
import com.pyamsoft.pydroid.util.ifNotCancellation
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
  private fun togglePowerSaving(enable: Boolean, type: String): Boolean {
    val value = if (enable) 1 else 0
    Timber.d("Attempt power-saving via settings: $type")
    return Settings.Global.putInt(resolver, "low_power", value)
  }

  private fun resetRunContext() {
    ignorePowerWhenAlreadyInPowerSavingMode = false
  }

  @CheckResult
  private suspend fun ignoreIfDeviceIsAlreadyPowerSaving(enable: Boolean): Boolean {
    if (enable) {
      resetRunContext()

      if (preferences.isIgnoreInPowerSavingMode()) {
        if (powerManager.isPowerSaveMode) {
          ignorePowerWhenAlreadyInPowerSavingMode = true
          Timber.d("Do not act while device is already in power saving mode ")
          return true
        }
      }
    } else {
      val shouldIgnore = ignorePowerWhenAlreadyInPowerSavingMode

      // Set back to false
      resetRunContext()

      if (shouldIgnore) {
        Timber.d("Service is ignoring command while device is in power-saving mode")
        return true
      }
    }

    return false
  }

  @CheckResult
  private fun isBatteryCharging(): Boolean {
    val statusIntent: Intent? = context.registerReceiver(null, BATTERY_STATUS_INTENT_FILTER)
    val batteryStatus =
        statusIntent?.getIntExtra(
            BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
            ?: BatteryManager.BATTERY_STATUS_UNKNOWN
    return batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
        batteryStatus == BatteryManager.BATTERY_STATUS_FULL
  }

  @CheckResult
  override suspend fun attemptPowerSaving(enable: Boolean): Boolean =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()

        val attemptType = attemptTypeString(enable)
        return@withContext coroutineScope {
          if (!permissions.hasSecureSettingsPermission()) {
            Timber.w("No power related work without WRITE_SECURE_SETTINGS permission: $attemptType")
            resetRunContext()
            return@coroutineScope false
          }

          if (!preferences.isPowerSavingEnabled()) {
            Timber.w("No power related work while job is disabled: $attemptType")
            resetRunContext()
            return@coroutineScope false
          }

          if (ignoreIfDeviceIsAlreadyPowerSaving(enable)) {
            Timber.w("Command was ignored because of power-saving mode: $attemptType")
            return@coroutineScope false
          }

          if (isBatteryCharging()) {
            Timber.w("No power related work while device is charging: $attemptType")
            resetRunContext()
            return@coroutineScope false
          }

          try {
            Timber.d("Attempt power saving: $attemptType")
            return@coroutineScope togglePowerSaving(
                enable = enable,
                type = attemptType,
            )
          } catch (e: Throwable) {
            e.ifNotCancellation {
              Timber.e(e, "Power saving error. Attempted: $attemptType")
              resetRunContext()
              return@coroutineScope false
            }
          }
        }
      }

  companion object {

    private val BATTERY_STATUS_INTENT_FILTER = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    @JvmStatic
    @CheckResult
    private fun attemptTypeString(enable: Boolean): String {
      return if (enable) "POWER-SAVE" else "POWER-NORMAL"
    }
  }
}
