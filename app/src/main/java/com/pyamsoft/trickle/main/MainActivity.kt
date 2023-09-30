package com.pyamsoft.trickle.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.PYDroidActivityDelegate
import com.pyamsoft.pydroid.ui.app.installPYDroid
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.ui.util.fillUpToPortraitHeight
import com.pyamsoft.pydroid.util.PermissionRequester
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.R
import com.pyamsoft.trickle.TrickleTheme
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.service.notification.PermissionRequests
import com.pyamsoft.trickle.service.notification.PermissionResponses
import com.pyamsoft.trickle.ui.InstallPYDroidExtras
import com.pyamsoft.trickle.ui.LANDSCAPE_MAX_WIDTH
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

  @JvmField @Inject internal var viewModel: ThemeViewModeler? = null

  @JvmField @Inject internal var permissionRequestBus: EventBus<PermissionRequests>? = null
  @JvmField @Inject internal var permissionResponseBus: EventBus<PermissionResponses>? = null

  @JvmField @Inject internal var notificationPermissionRequester: PermissionRequester? = null

  private var notificationRequester: PermissionRequester.Requester? = null
  private var pydroid: PYDroidActivityDelegate? = null

  private fun initializePYDroid() {
    pydroid =
        installPYDroid(
            provider =
                object : ChangeLogProvider {

                  override val applicationIcon = R.mipmap.ic_launcher

                  override val changelog = buildChangeLog {
                    change("Tip Jar popup is less frequent")
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupActivity()

    val vm = viewModel.requireNotNull()
    val appName = getString(R.string.app_name)

    setContent {
      val theme by vm.theme.collectAsStateWithLifecycle()

      TrickleTheme(
          theme = theme,
      ) {
        SystemBars()
        InstallPYDroidExtras(
            modifier = Modifier.fillUpToPortraitHeight().widthIn(max = LANDSCAPE_MAX_WIDTH),
            appName = appName,
        )
        MainEntry(
            modifier = Modifier.fillMaxSize(),
            appName = appName,
            onShowInAppRating = { handleShowInAppRating() },
        )
      }
    }
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

    notificationRequester?.unregister()

    permissionRequestBus = null
    permissionResponseBus = null
    notificationPermissionRequester = null
    notificationRequester = null
    viewModel = null
  }
}
