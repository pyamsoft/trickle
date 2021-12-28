package com.pyamsoft.trickle.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
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

  @Inject @JvmField internal var launcher: ServiceLauncher? = null

  private fun watchScreen() {
    receiver = receiver ?: ScreenReceiver.register(this)
  }

  private fun updatePowerSaving(intent: Intent?) {
    serviceScope.launch(context = Dispatchers.Main) {
      launcher.requireNotNull().also { l ->
        updatePowerPreference(l, intent)
        l.updateNotification()
      }
    }
  }

  private suspend fun updatePowerPreference(l: ServiceLauncher, intent: Intent?) {
    // If the intent extra is passed, we can update the preference
    if (intent == null) {
      return
    }

    if (!intent.hasExtra(ServiceLauncher.KEY_TOGGLE_POWER_SAVING)) {
      return
    }

    // If we fail to find it, turn off management
    val enable = intent.getBooleanExtra(ServiceLauncher.KEY_TOGGLE_POWER_SAVING, false)
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

    launcher.requireNotNull().createNotification(this)
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
    launcher?.stopNotification(this)

    launcher = null
    receiver = null

    serviceScope.cancel()

    stopSelf()
  }

  companion object {

    @JvmStatic
    fun start(context: Context) {
      val appContext = context.applicationContext
      val service = Intent(appContext, MonitorService::class.java)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        appContext.startForegroundService(service)
      } else {
        appContext.startService(service)
      }
    }
  }
}
