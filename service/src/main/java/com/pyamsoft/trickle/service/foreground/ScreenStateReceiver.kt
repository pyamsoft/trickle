package com.pyamsoft.trickle.service.foreground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.trickle.battery.PowerSaver
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Singleton
internal class ScreenStateReceiver
@Inject
internal constructor(
    private val context: Context,
    private val enforcer: ThreadEnforcer,
    private val saver: PowerSaver,
) : BroadcastReceiver(), ScreenReceiver {

  private val receiverScope by lazy {
    CoroutineScope(
        context = SupervisorJob() + Dispatchers.Default + CoroutineName(this::class.java.name),
    )
  }
  private val mutex = Mutex()
  private val registered = MutableStateFlow(false)

  private fun unregister() {
    enforcer.assertOnMainThread()

    Timber.d { "Unregister Screen Receiver" }
    context.unregisterReceiver(this)
  }

  override suspend fun register() {
    val self = this

    withContext(context = Dispatchers.Default) {
      if (registered.compareAndSet(expect = false, update = true)) {
        try {
          // Hold this here until the coroutine is cancelled
          coroutineScope {
            withContext(context = Dispatchers.Main) {
              Timber.d { "Register Screen Receiver" }
              ContextCompat.registerReceiver(
                  context,
                  self,
                  INTENT_FILTER,
                  ContextCompat.RECEIVER_NOT_EXPORTED,
              )
            }

            // And suspend until we are done
            awaitCancellation()
          }
        } finally {
          withContext(context = NonCancellable) {
            if (registered.compareAndSet(expect = true, update = false)) {
              withContext(context = Dispatchers.Main) { unregister() }
            }
          }
        }
      }
    }
  }

  private suspend fun handleScreenOff() =
      withContext(context = Dispatchers.Default) {
        // NonCancellable so we cannot have this operation stop partially done
        withContext(context = NonCancellable) {
          mutex.withLock {
            // Hold a mutex to make sure we don't have parallel operations
            // when dealing with system Power settings
            Timber.d { "SCREEN_OFF: Enable power_saving" }

            // Delay a bit so the system can catch up
            delay(300L)

            saver.setSystemPowerSaving(enable = true)
          }
        }
      }

  private suspend fun handleScreenOn() =
      withContext(context = Dispatchers.Default) {
        // NonCancellable so we cannot have this operation stop partially done
        withContext(context = NonCancellable) {
          mutex.withLock {
            // Hold a mutex to make sure we don't have parallel operations
            // when dealing with system Power settings
            Timber.d { "SCREEN_ON: Disable power_saving" }

            // Delay a bit so the system can catch up
            delay(300L)

            saver.setSystemPowerSaving(enable = false)
          }
        }
      }

  override fun onReceive(context: Context, intent: Intent) {
    // Go async in case scope work takes a long time
    val pending = goAsync()

    receiverScope.launch(context = Dispatchers.Default) {
      try {
        when (val action = intent.action) {
          Intent.ACTION_SCREEN_OFF -> handleScreenOff()
          Intent.ACTION_SCREEN_ON -> handleScreenOn()
          else -> {
            Timber.w { "Unhandled intent action: $action" }
          }
        }
      } finally {
        withContext(context = Dispatchers.Main) {
          // Mark BR as finished
          pending.finish()
        }
      }
    }
  }

  companion object {

    private val INTENT_FILTER =
        IntentFilter().apply {
          addAction(Intent.ACTION_SCREEN_OFF)
          addAction(Intent.ACTION_SCREEN_ON)
        }
  }
}
