package com.pyamsoft.trickle.service.foreground

import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.trickle.battery.saver.PowerSaverManager
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Singleton
internal class ScreenStateResponder
@Inject
internal constructor(
    private val saverManager: PowerSaverManager,
    private val screenBus: EventBus<ScreenState>,
) : ScreenReceiver {

  private val mutex = Mutex()
  private val mostRecentAction = MutableStateFlow(false)
  private val registered = MutableStateFlow(false)

  private fun unregister() {
    Timber.d { "Unregister ScreenStateResponder" }
  }

  private fun CoroutineScope.watchForScreenEvents() {
    val scope = this
    screenBus.also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { event ->
          when (event) {
            ScreenState.SCREEN_ON -> handleScreenOn()
            ScreenState.SCREEN_OFF -> handleScreenOff()
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
            if (mostRecentAction.compareAndSet(expect = false, update = true)) {
              // Hold a mutex to make sure we don't have parallel operations
              // when dealing with system Power settings
              Timber.d { "SCREEN_OFF: Enable power_saving" }

              // Delay a bit so the system can catch up
              delay(300L)

              saverManager.savePower(enable = true)
            } else {
              Timber.w { "Most recent action was ENABLE power_saving, ignore repeat action" }
            }
          }
        }
      }

  private suspend fun handleScreenOn() =
      withContext(context = Dispatchers.Default) {
        // NonCancellable so we cannot have this operation stop partially done
        withContext(context = NonCancellable) {
          mutex.withLock {
            if (mostRecentAction.compareAndSet(expect = true, update = false)) {
              // Hold a mutex to make sure we don't have parallel operations
              // when dealing with system Power settings
              Timber.d { "SCREEN_ON: Disable power_saving" }

              // Delay a bit so the system can catch up
              delay(300L)

              saverManager.savePower(enable = false)
            } else {
              Timber.w { "Most recent action was DISABLE power_saving, ignore repeat action" }
            }
          }
        }
      }

  override suspend fun register() {
    withContext(context = Dispatchers.Default) {
      if (registered.compareAndSet(expect = false, update = true)) {
        Timber.d { "Register ScreenStateResponder" }
        try {
          // Hold this here until the coroutine is cancelled
          coroutineScope {

            // Listen for A14 workarounds
            watchForScreenEvents()

            // And suspend until we are done
            awaitCancellation()
          }
        } finally {
          withContext(context = NonCancellable) {
            if (registered.compareAndSet(expect = true, update = false)) {
              unregister()
            }
          }
        }
      }
    }
  }
}
