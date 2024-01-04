package com.pyamsoft.trickle.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Process
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivityDelegate
import com.pyamsoft.pydroid.ui.app.installPYDroid
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.ui.util.fillUpToPortraitSize
import com.pyamsoft.pydroid.util.PermissionRequester
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.R
import com.pyamsoft.trickle.TrickleTheme
import com.pyamsoft.trickle.battery.optimize.BatteryOptimizer
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.service.A14WorkAround
import com.pyamsoft.trickle.service.ServicePreferences
import com.pyamsoft.trickle.service.notification.PermissionRequests
import com.pyamsoft.trickle.service.notification.PermissionResponses
import com.pyamsoft.trickle.service.registerToLifecycle
import com.pyamsoft.trickle.ui.InstallPYDroidExtras
import com.pyamsoft.trickle.ui.LANDSCAPE_MAX_WIDTH
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

  @JvmField @Inject internal var viewModel: ThemeViewModeler? = null

  @JvmField @Inject internal var permissionRequestBus: EventBus<PermissionRequests>? = null
  @JvmField @Inject internal var permissionResponseBus: EventBus<PermissionResponses>? = null

  @JvmField @Inject internal var notificationPermissionRequester: PermissionRequester? = null

  @JvmField @Inject internal var a14WorkAround: A14WorkAround? = null
  @JvmField @Inject internal var batteryOptimizer: BatteryOptimizer? = null
  @JvmField @Inject internal var servicePreferences: ServicePreferences? = null

  private var notificationRequester: PermissionRequester.Requester? = null
  private var pydroid: PYDroidActivityDelegate? = null

  private fun initializePYDroid() {
    pydroid =
        installPYDroid(
            provider =
                object : ChangeLogProvider {

                  override val applicationIcon = R.mipmap.ic_launcher

                  override val changelog = buildChangeLog {
                    feature(
                        "Android 14: Better compatibility by always forcing the service into the background for consistency.")
                    bugfix(
                        "Android 14: You should use the new \"Force Background\" tweak to ensure the Service runs consistently.")
                    bugfix(
                        "Ignore repeated screen on or off events while the screen is already on or off.")
                    change(
                        "Restructure implementation on screen event receiver to more consistently listen for on and off.")
                  }
                },
        )
  }

  private fun registerToRespondToPermissionRequests() {
    permissionRequestBus.requireNotNull().also { f ->
      lifecycleScope.launch(context = Dispatchers.Default) {
        f.collect { req ->
          when (req) {
            is PermissionRequests.Notification -> {
              notificationRequester.requireNotNull().requestPermissions()
            }
          }
        }
      }
    }
  }

  private fun registerToSendPermissionResults() {
    notificationRequester?.unregister()
    notificationRequester =
        notificationPermissionRequester.requireNotNull().registerRequester(this) { granted ->
          if (granted) {
            Timber.d { "Notification permission granted" }

            // Broadcast in the background
            lifecycleScope.launch(context = Dispatchers.Default) {
              permissionResponseBus.requireNotNull().emit(PermissionResponses.Notification)
            }
          } else {
            Timber.w { "Notification permission not granted" }
          }
        }
  }

  private fun handleShowInAppRating() {
    pydroid?.loadInAppRating()
  }

  private fun setupActivity() {
    // Setup PYDroid first
    initializePYDroid()

    // Create and initialize the ObjectGraph
    val component = ObjectGraph.ApplicationScope.retrieve(this).plusMainComponent().create()
    component.inject(this)
    ObjectGraph.ActivityScope.install(this, component)

    // Then register for any permissions
    registerToSendPermissionResults()
    registerToRespondToPermissionRequests()

    // Finally update the View
    stableLayoutHideNavigation()
  }

  /**
   * On Android 14, we sometimes get into a state where we are still alive and service is running
   * but we can't actually receive Screen state intents probably due to system changes in A14. We
   * can, for some reason though, still receive Activity callbacks.
   *
   * Register on the DisplayManager and watch for the display state to change.
   */
  private fun android14BackgroundActivityWorkaround() {
    a14WorkAround.requireNotNull().registerToLifecycle(owner = this)

    val forceBackgroundOnStop = MutableStateFlow(false)

    val bo = batteryOptimizer.requireNotNull()
    lifecycle.addObserver(
        object : DefaultLifecycleObserver {

          private fun attemptForceBackground() {
            Timber.d { "Attempt force into background" }
            lifecycleScope.launch(context = Dispatchers.Default) {
              if (bo.isOptimizationsIgnored()) {
                withContext(context = Dispatchers.Main) {
                  android14BackgroundServiceQuirkWorkaround()
                }
              } else {
                Timber.w { "Not forcing background without ignoring battery settings" }
              }
            }
          }

          override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)

            if (isFinishing) {
              attemptForceBackground()
            }
          }

          override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            if (forceBackgroundOnStop.value) {
              Timber.d { "Force Background on stop enabled!" }
              attemptForceBackground()
            }
          }

          override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            owner.lifecycle.removeObserver(this)
          }
        },
    )

    servicePreferences.requireNotNull().listenAlwaysBackground().also { f ->
      lifecycleScope.launch(context = Dispatchers.Default) {
        f.collect { forceBackgroundOnStop.value = it }
      }
    }
  }

  /**
   * For some reason on A14, going into the background will produce inconsistent service callbacks
   *
   * But if we kill our process with Always Alive On, it will restart and receive consistent events.
   * Do that.
   */
  private fun android14BackgroundServiceQuirkWorkaround() {
    Timber.d { "A14 Kill self for background service workaround" }
    Process.killProcess(Process.myPid())
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupActivity()

    val vm = viewModel.requireNotNull()
    val appName = getString(R.string.app_name)

    setContent {
      val theme by vm.theme.collectAsStateWithLifecycle()

      SaveStateDisposableEffect(vm)

      TrickleTheme(
          theme = theme,
      ) {
        SystemBars()
        InstallPYDroidExtras(
            modifier = Modifier.fillUpToPortraitSize().widthIn(max = LANDSCAPE_MAX_WIDTH),
            appName = appName,
        )
        MainEntry(
            modifier = Modifier.fillMaxSize(),
            appName = appName,
            onShowInAppRating = { handleShowInAppRating() },
            onForceBackground = { android14BackgroundServiceQuirkWorkaround() },
        )
      }
    }

    android14BackgroundActivityWorkaround()
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
  }

  override fun onResume() {
    super.onResume()
    viewModel.requireNotNull().handleSyncDarkTheme(this)
    reportFullyDrawn()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    viewModel?.handleSyncDarkTheme(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.d { "Destroy!" }

    notificationRequester?.unregister()

    permissionRequestBus = null
    permissionResponseBus = null
    notificationPermissionRequester = null
    notificationRequester = null
    a14WorkAround = null
    batteryOptimizer = null
    servicePreferences = null
    viewModel = null
  }
}
