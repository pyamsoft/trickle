package com.pyamsoft.trickle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.trickle.TrickleComponent
import com.pyamsoft.trickle.receiver.ScreenReceiver
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

class MonitorService : Service() {

  /** CoroutineScope for the Service level */
  private val serviceScope = MainScope()

  private var receiver: ScreenReceiver.Unregister? = null

  @Inject @JvmField internal var notification: ServiceNotification? = null

  private fun watchScreen() {
    receiver = receiver ?: ScreenReceiver.register(this)
  }

  private fun updatePowerSaving(intent: Intent?) {
    serviceScope.launch(context = Dispatchers.Main) {
      notification.requireNotNull().also { l ->
        updatePowerPreference(l, intent)
        l.updateNotification()
      }
    }
  }

  private suspend fun updatePowerPreference(l: ServiceNotification, intent: Intent?) {
    // If the intent extra is passed, we can update the preference
    if (intent == null) {
      return
    }

    if (!intent.hasExtra(ServiceNotification.KEY_TOGGLE_POWER_SAVING)) {
      return
    }

    // If we fail to find it, turn off management
    val enable = intent.getBooleanExtra(ServiceNotification.KEY_TOGGLE_POWER_SAVING, false)
    l.togglePowerSavingEnabled(enable)
  }

  override fun onBind(intent: Intent?): IBinder? {
    throw IllegalStateException("Not a bound service")
  }

  override fun onCreate() {
    super.onCreate()
    Injector.obtainFromApplication<TrickleComponent>(this)
        .plusServiceComponent()
        .create()
        .inject(this)

    notification.requireNotNull().createNotification(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    watchScreen()
    updatePowerSaving(intent)

    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.d("Stop notification in foreground and kill service")

    receiver?.unregister()
    notification?.stopNotification(this)

    notification = null
    receiver = null

    serviceScope.cancel()

    stopSelf()
  }
}
