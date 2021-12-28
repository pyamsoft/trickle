package com.pyamsoft.trickle.process.workmanager.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.hasKeyWithValueOfType
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.trickle.process.work.PowerSaverInjector
import com.pyamsoft.trickle.process.workmanager.WorkManagerProcessScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class PowerSavingWorker
internal constructor(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context.applicationContext, params) {

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

        val injector = PowerSaverInjector(applicationContext)
        return@withContext if (injector.powerSaver().attemptPowerSaving(enable)) {
          Result.success()
        } else {
          Result.failure()
        }
      }
}
