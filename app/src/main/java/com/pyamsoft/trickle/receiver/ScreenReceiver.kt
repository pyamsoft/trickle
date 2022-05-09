package com.pyamsoft.trickle.receiver

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.annotation.CheckResult
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.trickle.TrickleComponent
import com.pyamsoft.trickle.process.PowerPreferences
import com.pyamsoft.trickle.process.work.PowerSaver
import com.pyamsoft.trickle.receiver.ScreenReceiver.Unregister
import javax.inject.Inject
import kotlinx.coroutines.*
import timber.log.Timber

class ScreenReceiver : BroadcastReceiver() {

  @Inject @JvmField internal var powerSaver: PowerSaver? = null
  @Inject @JvmField internal var preferences: PowerPreferences? = null

  private val scope by lazy(LazyThreadSafetyMode.NONE) { MainScope() }

  private var powerManager: PowerManager? = null
  private var currentJob: Job? = null

  /**
   * When going down for power saving, we set this variable When coming back up from power saving we
   * read and unset this variable
   */
  private var ignorePowerWhenAlreadyInPowerSavingMode = false

  private fun resetRunContext() {
    Timber.d("Resetting run context")
    ignorePowerWhenAlreadyInPowerSavingMode = false
  }

  private fun inject(context: Context) {
    if (powerSaver != null) {
      return
    }

    Injector.obtainFromApplication<TrickleComponent>(context).inject(this)
    powerManager = context.applicationContext.getSystemService<PowerManager>().requireNotNull()
  }

  @CheckResult
  private suspend fun canActInPowerSavingMode(enable: Boolean): Boolean {
    if (enable) {
      // Reset for this time
      resetRunContext()

      // Check preference and device state
      if (preferences.requireNotNull().isIgnoreInPowerSavingMode()) {
        if (powerManager.requireNotNull().isPowerSaveMode) {
          ignorePowerWhenAlreadyInPowerSavingMode = true
          Timber.d("Power Saving is managed from outside, do not act")
          return false
        }
      }
    } else {
      // Retrieve previous value
      val shouldIgnore = ignorePowerWhenAlreadyInPowerSavingMode

      // Reset for next time
      resetRunContext()

      if (shouldIgnore) {
        Timber.d("Power Saving was set from outside, ignore any work from the app.")
        return false
      }
    }

    return true
  }

  @CheckResult
  private suspend fun canAct(enable: Boolean): Boolean {
    return canActInPowerSavingMode(enable = enable)
  }

  private fun doWork(context: Context, enable: Boolean) {
    inject(context)

    currentJob?.cancel()
    currentJob =
        scope.launch(context = Dispatchers.Default) {
          // Make sure we can act
          if (!canAct(enable = enable)) {
            return@launch
          }

          // Slight delay to allow device screen to turn on or off
          delay(500L)

          // Then act
          when (val result = powerSaver.requireNotNull().attemptPowerSaving(enable)) {
            is PowerSaver.State.Disabled -> Timber.d("Power Saving DISABLED")
            is PowerSaver.State.Enabled -> Timber.d("Power Saving ENABLED")
            is PowerSaver.State.Failure -> Timber.w(result.throwable, "Power Saving Error")
          }
        }
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_SCREEN_ON) {
      doWork(context, enable = false)
    } else if (intent.action == Intent.ACTION_SCREEN_OFF) {
      doWork(context, enable = true)
    }
  }

  companion object {

    private val filter =
        IntentFilter().apply {
          addAction(Intent.ACTION_SCREEN_ON)
          addAction(Intent.ACTION_SCREEN_OFF)
        }

    /** Service instead of context so that we scope registratin lifecycle to the service */
    @JvmStatic
    fun register(service: Service): Unregister {
      val receiver = ScreenReceiver()
      Timber.d("Screen Receiver registered")
      service.registerReceiver(receiver, filter)
      return Unregister { service.unregisterReceiver(receiver) }
    }
  }

  fun interface Unregister {

    fun unregister()
  }
}
