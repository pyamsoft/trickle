package com.pyamsoft.trickle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.receiver.ScreenReceiver
import javax.inject.Inject
import timber.log.Timber

class MonitorService : Service() {

  private var receiver: ScreenReceiver.Unregister? = null

  @Inject @JvmField internal var notification: ServiceNotification? = null
  @Inject @JvmField internal var serviceHandler: ServiceHandler? = null

  private fun ensureWatchingScreen() {
    receiver = receiver ?: ScreenReceiver.register(this)
  }

  @CheckResult
  private fun getTogglePowerSaving(intent: Intent?): Boolean? {
    // If the intent extra is passed, we can update the preference
    if (intent == null) {
      return null
    }

    if (!intent.hasExtra(ServiceNotification.KEY_TOGGLE_POWER_SAVING)) {
      return null
    }

    // If we fail to find it, turn off management
    return intent.getBooleanExtra(ServiceNotification.KEY_TOGGLE_POWER_SAVING, false)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    ObjectGraph.ApplicationScope.retrieve(this).plusServiceComponent().create().inject(this)

    // Start notification first for android O immediately
    notification.requireNotNull().createNotification(this)

    // Register for screen events
    ensureWatchingScreen()

    // Handler
    serviceHandler.requireNotNull().bind(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    ensureWatchingScreen()

    getTogglePowerSaving(intent)?.also { enable ->
      serviceHandler.requireNotNull().toggle(this, enable)
    }

    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()

    Timber.d("Destroy service")

    notification?.stopNotification(this)
    serviceHandler?.destroy()
    receiver?.unregister()

    serviceHandler = null
    notification = null
    receiver = null
  }
}
