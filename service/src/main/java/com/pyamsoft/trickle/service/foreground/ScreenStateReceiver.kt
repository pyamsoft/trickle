package com.pyamsoft.trickle.service.foreground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.ThreadEnforcer
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
internal class ScreenStateReceiver
@Inject
internal constructor(
    private val context: Context,
    private val enforcer: ThreadEnforcer,
    private val screenStateBus: EventBus<ScreenState>,
) : BroadcastReceiver(), ScreenReceiver {

  private val receiverScope by lazy {
    CoroutineScope(
        context = SupervisorJob() + Dispatchers.Default + CoroutineName(this::class.java.name),
    )
  }

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
              // Don't use flag because we are listening for system broadcasts
              // https://developer.android.com/about/versions/14/behavior-changes-14#system-broadcasts
              context.registerReceiver(self, INTENT_FILTER)
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

  override fun onReceive(context: Context, intent: Intent) {
    // Go async in case scope work takes a long time
    val pending = goAsync()

    receiverScope.launch(context = Dispatchers.Default) {
      try {
        when (val action = intent.action) {
          Intent.ACTION_SCREEN_OFF -> {
            Timber.d { "Broadcast: SCREEN_OFF" }
            screenStateBus.emit(ScreenState.SCREEN_OFF)
          }
          Intent.ACTION_SCREEN_ON -> {
            Timber.d { "Broadcast: SCREEN_ON" }
            screenStateBus.emit(ScreenState.SCREEN_ON)
          }
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
