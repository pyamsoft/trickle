package com.pyamsoft.trickle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MonitorService : Service() {

  @Inject @JvmField internal var runner: ServiceRunner? = null
  @Inject @JvmField internal var a14WorkAround: A14WorkAround? = null

  private val selfService by lazy { Intent(applicationContext, this::class.java) }

  private var a14Unregister: A14WorkAround.Unregister? = null
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

  /**
   * On Android 14, we sometimes get into a state where we are still alive and service is running
   * but we can't actually receive Screen state intents probably due to system changes in A14. We
   * can, for some reason though, still receive Activity callbacks.
   *
   * Register on the DisplayManager and watch for the display state to change.
   */
  private fun android14BackgroundActivityWorkaround() {
    a14Unregister?.unregister()
    a14Unregister = a14WorkAround.requireNotNull().register(scope = ensureScope())
  }

  /**
   * On Android 14, after Swipe Away we sometimes Create but dont call onStartCommand
   *
   * Work around this by "starting ourselves"
   */
  private fun startSelf() {
    startService(selfService)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    ObjectGraph.ApplicationScope.retrieve(this).plusServiceComponent().create().inject(this)
    Timber.d { "Creating service" }

    android14BackgroundActivityWorkaround()

    // A14 quirks when restarting a sticky service
    startSelf()
  }

  /**
   * If the app is in the background, this will not run unless the app sets Battery Optimization off
   */
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d { "Starting Service" }
    // Each time the service starts/restarts we use the fact that it is tied to the Android OS
    // lifecycle to restart the launcher which does all the Proxy lifting.
    startRunner()

    // Just start sticky here
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.d { "Destroying service" }

    scope?.cancel()
    a14Unregister?.unregister()

    scope = null
    runner = null
    a14WorkAround = null
    a14Unregister = null
  }
}
