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
import timber.log.Timber

class MonitorService : Service() {

  private var receiver: ScreenReceiver.Unregister? = null

  @Inject @JvmField internal var launcher: ServiceLauncher? = null

  private fun watchScreen() {
    receiver = receiver ?: ScreenReceiver.register(this)
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
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    watchScreen()
    launcher.requireNotNull().createNotification(this)
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.d("Stop notification in foreground and kill service")

    receiver?.unregister()
    launcher?.stopNotification(this)

    launcher = null
    receiver = null

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
