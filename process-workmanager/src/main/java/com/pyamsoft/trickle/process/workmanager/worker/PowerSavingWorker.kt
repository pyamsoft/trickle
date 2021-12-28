package com.pyamsoft.trickle.process.workmanager.worker

import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.hasKeyWithValueOfType
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.trickle.process.workmanager.WorkManagerProcessScheduler
import com.pyamsoft.trickle.process.workmanager.ifNotCancellation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class PowerSavingWorker
internal constructor(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context.applicationContext, params) {

  private val batteryManager by
      lazy(LazyThreadSafetyMode.NONE) {
        context.applicationContext.getSystemService<BatteryManager>().requireNotNull()
      }

  /** This should work if we have WRITE_SECURE_SETTINGS */
  @CheckResult
  private fun togglePowerSaving(context: Context, enable: Boolean): Boolean {
    val value = if (enable) 1 else 0
    Timber.d("Attempt ${if (enable) "ENABLE" else "DISABLE"} via settings")
    val resolver = context.applicationContext.contentResolver
    return Settings.Global.putInt(resolver, "low_power", value)
  }

  override suspend fun doWork(): Result =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()
        val tag = tags.joinToString(",")
        val key = WorkManagerProcessScheduler.KEY_ENABLE

        if (!inputData.hasKeyWithValueOfType<Boolean>(key)) {
          Timber.w("Work missing Enable value: $tag")
          return@withContext Result.failure()
        }

        // DefaultValue is TRUE for "no power saving"
        val enable = inputData.getBoolean(WorkManagerProcessScheduler.KEY_ENABLE, true)

        return@withContext coroutineScope {
          if (batteryManager.isCharging) {
            Timber.w("Do not attempt any power related work while device is charging")
            return@coroutineScope Result.success()
          }

          Timber.d("Attempt work: $tag")
          try {
            if (togglePowerSaving(applicationContext, enable)) {
              Timber.d("Work success: $tag")
              Result.success()
            } else {
              Timber.w("Work failed: $tag")
              Result.failure()
            }
          } catch (e: Throwable) {
            return@coroutineScope e.ifNotCancellation { err ->
              Timber.e(err, "Work error: $tag")
              Result.failure()
            }
          } finally {
            Timber.d("Worker has been completed: $tag")
          }
        }
      }
}
