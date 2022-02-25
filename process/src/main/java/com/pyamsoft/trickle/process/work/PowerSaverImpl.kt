package com.pyamsoft.trickle.process.work

import android.content.Context
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
  private fun togglePowerSaving(enable: Boolean): Boolean {
    val value = if (enable) 1 else 0
    Timber.d("Attempt ${if (enable) "ENABLE" else "DISABLE"} via settings")
    return Settings.Global.putInt(resolver, "low_power", value)
  }

  @CheckResult
  private suspend fun ignoreIfDeviceIsAlreadyPowerSaving(enable: Boolean): Boolean {
    if (enable) {
      if (preferences.isIgnoreInPowerSavingMode()) {
        if (powerManager.isPowerSaveMode) {
          ignorePowerWhenAlreadyInPowerSavingMode = true
          Timber.d("Do not act while device is already in power saving mode ")
          return true
        } else {
          ignorePowerWhenAlreadyInPowerSavingMode = false
        }
      } else {
        ignorePowerWhenAlreadyInPowerSavingMode = false
      }
    } else {
      val check = ignorePowerWhenAlreadyInPowerSavingMode

      // Set back to false
      ignorePowerWhenAlreadyInPowerSavingMode = false

      if (check) {
        Timber.d("Service is ignoring command while device is in power-saving mode")
        return true
      }
    }

    return false
  }

  @CheckResult
  override suspend fun attemptPowerSaving(enable: Boolean): Boolean =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()

        return@withContext coroutineScope {
          if (!permissions.hasSecureSettingsPermission()) {
            Timber.w("No power related work without WRITE_SECURE_SETTINGS permission")
            // Can't do anything
            return@coroutineScope false
          }

          if (batteryManager.isCharging) {
            Timber.w("No power related work while device is charging")
            return@coroutineScope false
          }

          if (!preferences.isPowerSavingEnabled()) {
            Timber.w("No power related work while job is disabled")
            return@coroutineScope false
          }

          if (ignoreIfDeviceIsAlreadyPowerSaving(enable)) {
            Timber.w("Command was ignored because of power-saving mode")
            return@coroutineScope false
          }

          Timber.d("Attempt power saving: ${if (enable) "ON" else "OFF"}")
          try {
            return@coroutineScope togglePowerSaving(enable = enable)
          } catch (e: Throwable) {
            e.ifNotCancellation {
              Timber.e(e, "Power saving error. Attempted: ${if (enable) "ON" else "OFF"}")
              return@coroutineScope false
            }
          }
        }
      }
}
