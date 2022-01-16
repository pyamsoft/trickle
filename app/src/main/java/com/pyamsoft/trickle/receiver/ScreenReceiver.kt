package com.pyamsoft.trickle.receiver

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.trickle.TrickleComponent
import com.pyamsoft.trickle.process.work.PowerSaver
import com.pyamsoft.trickle.receiver.ScreenReceiver.Unregister
import javax.inject.Inject
import kotlinx.coroutines.*
import timber.log.Timber

class ScreenReceiver : BroadcastReceiver() {

  @Inject @JvmField internal var powerSaver: PowerSaver? = null

  private val scope by lazy(LazyThreadSafetyMode.NONE) { MainScope() }
  private var currentJob: Job? = null

  private fun inject(context: Context) {
    if (powerSaver != null) {
      return
    }

    Injector.obtainFromApplication<TrickleComponent>(context).inject(this)
  }

  private fun doWork(context: Context, enable: Boolean) {
    inject(context)

    currentJob?.cancel()
    currentJob =
        scope.launch(context = Dispatchers.Default) {
          delay(500L)
          powerSaver.requireNotNull().attemptPowerSaving(enable)
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
