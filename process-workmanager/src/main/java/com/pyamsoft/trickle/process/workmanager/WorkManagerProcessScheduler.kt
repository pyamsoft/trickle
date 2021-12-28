package com.pyamsoft.trickle.process.workmanager

import android.content.Context
import androidx.annotation.CheckResult
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import com.google.common.util.concurrent.ListenableFuture
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.trickle.process.ProcessScheduler
import com.pyamsoft.trickle.process.workmanager.worker.PowerSavingWorker
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
internal class WorkManagerProcessScheduler
@Inject
internal constructor(
    private val context: Context,
) : ProcessScheduler {

  @CheckResult
  private fun workManager(): WorkManager {
    Enforcer.assertOffMainThread()
    return WorkManager.getInstance(context)
  }

  private suspend fun queuePowerSaving(enable: Boolean) {
    Enforcer.assertOffMainThread()

    val tag = POWER_SAVING_TAG
    cancelPowerSaving()

    val work = getWorkClass()
    val request = createWork(work, tag, createInputData(enable))

    workManager().enqueue(request)
    Timber.d("Queue power saving work[${request.id}]: ENABLE=${enable}")
  }

  private suspend fun cancelPowerSaving() =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()
        workManager().cancelAllWorkByTag(POWER_SAVING_TAG).await()
      }

  override suspend fun cancel() =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()
        workManager().cancelAllWork().await()
      }

  override suspend fun schedulePowerSaving(enable: Boolean) =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()
        queuePowerSaving(enable)
      }

  companion object {

    private const val POWER_SAVING_TAG = "power_saving_tag"

    internal const val KEY_ENABLE = "key_enable"

    private val alerterExecutor = Executor { it.run() }

    private suspend fun Operation.await() {
      Enforcer.assertOffMainThread()
      this.result.await()
    }

    // Copied out of androidx.work.ListenableFuture
    // since this extension is library private otherwise...
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun <R> ListenableFuture<R>.await(): R {
      Enforcer.assertOffMainThread()

      // Fast path
      if (this.isDone) {
        try {
          return this.get()
        } catch (e: ExecutionException) {
          throw e.cause ?: e
        }
      }

      return suspendCancellableCoroutine { continuation ->
        Enforcer.assertOffMainThread()
        this.addListener(
            {
              Enforcer.assertOffMainThread()
              try {
                continuation.resume(this.get())
              } catch (throwable: Throwable) {
                val cause = throwable.cause ?: throwable
                when (throwable) {
                  is CancellationException -> continuation.cancel(cause)
                  else -> continuation.resumeWithException(cause)
                }
              }
            },
            alerterExecutor,
        )
      }
    }

    @JvmStatic
    @CheckResult
    private fun createInputData(enable: Boolean): Data {
      return Data.Builder().apply { putBoolean(KEY_ENABLE, enable) }.build()
    }

    @JvmStatic
    @CheckResult
    private fun createWork(
        work: Class<out Worker>,
        tag: String,
        inputData: Data,
    ): WorkRequest {
      Enforcer.assertOffMainThread()
      return OneTimeWorkRequest.Builder(work)
          .addTag(tag)
          .setInputData(inputData)
          // Slight delay to make sure system is ready
          .setInitialDelay(1, TimeUnit.SECONDS)
          .build()
    }

    @JvmStatic
    @CheckResult
    private fun getWorkClass(): Class<out Worker> {
      // Basically, this is shit, but hey its Android!
      // Please make sure your alarms use a class that implements a worker, thanks.
      @Suppress("UNCHECKED_CAST") return PowerSavingWorker::class.java as Class<out Worker>
    }
  }
}
