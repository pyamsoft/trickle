package com.pyamsoft.trickle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.trickle.ObjectGraph
import javax.inject.Inject
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

class MonitorService : Service() {

  @Inject @JvmField internal var runner: ServiceRunner? = null

  private var scope: CoroutineScope? = null

  @CheckResult
  private fun makeScope(): CoroutineScope {
    return CoroutineScope(
        context = SupervisorJob() + Dispatchers.Default + CoroutineName(this::class.java.name),
    )
  }

  @CheckResult
  private fun ensureScope(): CoroutineScope {
    return (scope ?: makeScope()).also { scope = it }
  }

  private fun startRunner() {
    ensureScope().launch(context = Dispatchers.Default) { runner.requireNotNull().start() }
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    ObjectGraph.ApplicationScope.retrieve(this).plusServiceComponent().create().inject(this)
    Timber.d("Creating service")
  }

  /**
   * If the app is in the background, this will not run unless the app sets Battery Optimization off
   */
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // Each time the service starts/restarts we use the fact that it is tied to the Android OS
    // lifecycle to restart the launcher which does all the Proxy lifting.
    startRunner()

    // Just start sticky here
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.d("Destroying service")

    scope?.cancel()

    scope = null
    runner = null
  }
}
