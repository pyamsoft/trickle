package com.pyamsoft.trickle.service

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.view.Display
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.service.A14WorkAround.Unregister
import com.pyamsoft.trickle.service.foreground.ScreenState
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
internal class DefaultA14WorkAround
@Inject
internal constructor(
    context: Context,
    private val screenStateBus: EventBus<ScreenState>,
) : A14WorkAround {

  private val displayManager by lazy { context.getSystemService<DisplayManager>().requireNotNull() }
  private val uiHandler by lazy { Handler(Looper.getMainLooper()) }

  /**
   * On Android 14, we sometimes get into a state where we are still alive and service is running
   * but we can't actually receive Screen state intents probably due to system changes in A14. We
   * can, for some reason though, still receive Activity callbacks.
   *
   * Register on the DisplayManager and watch for the display state to change.
   */
  override fun register(scope: CoroutineScope): Unregister {
    var previousDisplayState = -1

    val listener =
        object : DisplayManager.DisplayListener {
          override fun onDisplayAdded(displayId: Int) {}

          override fun onDisplayRemoved(displayId: Int) {}

          override fun onDisplayChanged(displayId: Int) {
            if (displayId == MAIN_DISPLAY_ID) {
              val currentState = displayManager.getDisplay(displayId).state
              if (currentState != previousDisplayState) {
                val w = screenStateBus.requireNotNull()
                scope.launch(context = Dispatchers.Default) {
                  previousDisplayState = currentState
                  if (currentState == Display.STATE_ON) {
                    Timber.d { "A14 Screen turned ON" }
                    w.emit(ScreenState.SCREEN_ON)
                  } else if (currentState == Display.STATE_OFF) {
                    Timber.d { "A14 Screen turned OFF" }
                    w.emit(ScreenState.SCREEN_OFF)
                  }
                }
              }
            }
          }
        }

    Timber.d { "Register Android 14 background broadcast workaround" }
    displayManager.registerDisplayListener(listener, uiHandler)
    return Unregister {
      Timber.d { "Unregister Android 14 background broadcast workaround" }
      displayManager.unregisterDisplayListener(listener)
    }
  }

  companion object {

    private const val MAIN_DISPLAY_ID = 0
  }
}
