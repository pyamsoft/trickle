package com.pyamsoft.trickle.process.work

import android.content.Context
import android.os.BatteryManager
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

  private val resolver by lazy(LazyThreadSafetyMode.NONE) { context.contentResolver }

  /** This should work if we have WRITE_SECURE_SETTINGS */
  @CheckResult
  private fun togglePowerSaving(enable: Boolean): Boolean {
    val value = if (enable) 1 else 0
    Timber.d("Attempt ${if (enable) "ENABLE" else "DISABLE"} via settings")
    return Settings.Global.putInt(resolver, "low_power", value)
  }

  @CheckResult
  override suspend fun attemptPowerSaving(enable: Boolean): Boolean =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()

        return@withContext coroutineScope {
          if (!permissions.hasSecureSettingsPermission()) {
            Timber.w(
                "Do not attempt any power related work without WRITE_SECURE_SETTINGS permission")
            // Can't do anything
            return@coroutineScope false
          }

          if (batteryManager.isCharging) {
            Timber.w("Do not attempt any power related work while device is charging")
            // Restore back to default state
            return@coroutineScope togglePowerSaving(enable = false)
          }

          if (!preferences.isPowerSavingEnabled()) {
            Timber.w("Do not attempt any power related work while job is disabled")
            // Restore back to default state
            return@coroutineScope togglePowerSaving(enable = false)
          }

          Timber.d("Attempt power saving")
          return@coroutineScope try {
            togglePowerSaving(enable = enable)
          } catch (e: Throwable) {
            e.ifNotCancellation {
              Timber.e(e, "Power saving error")
              false
            }
          }
        }
      }
}
