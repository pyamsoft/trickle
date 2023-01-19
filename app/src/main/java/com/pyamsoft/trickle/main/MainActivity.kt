package com.pyamsoft.trickle.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.installPYDroid
import com.pyamsoft.pydroid.ui.changelog.ChangeLogProvider
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.util.PermissionRequester
import com.pyamsoft.pydroid.util.doOnCreate
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.R
import com.pyamsoft.trickle.TrickleTheme
import com.pyamsoft.trickle.home.PermissionRequests
import com.pyamsoft.trickle.home.PermissionResponse
import com.pyamsoft.trickle.ui.icons.InstallPYDroidExtras
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

  @JvmField @Inject internal var viewModel: MainViewModeler? = null

  @JvmField @Inject internal var permissionRequestBus: EventBus<PermissionRequests>? = null
  @JvmField @Inject internal var permissionResponseBus: EventBus<PermissionResponse>? = null

  @JvmField @Inject internal var notificationPermissionRequester: PermissionRequester? = null

  private var notificationRequester: PermissionRequester.Requester? = null

  init {
    doOnCreate {
      installPYDroid(
          provider =
              object : ChangeLogProvider {

                override val applicationIcon = R.mipmap.ic_launcher_round

                override val changelog = buildChangeLog {
                  change("Full usage of Jetpack Compose")
                  bugfix("Big performance gains in Compose by correctly handling state")
                }
              },
      )
    }

    doOnCreate { registerToSendPermissionResults() }

    doOnCreate { registerToRespondToPermissionRequests() }
  }

  private fun registerToSendPermissionResults() {
    notificationRequester?.unregister()

    notificationRequester =
        notificationPermissionRequester.requireNotNull().registerRequester(this) { granted ->
          if (granted) {
            Timber.d("Notification permission granted")

            // Broadcast in the background
            lifecycleScope.launch(context = Dispatchers.IO) {
              permissionResponseBus.requireNotNull().send(PermissionResponse.RefreshNotification)
            }
          } else {
            Timber.w("Notification permission not granted")
          }
        }
  }

  private fun registerToRespondToPermissionRequests() {
    lifecycleScope.launch(context = Dispatchers.IO) {
      permissionRequestBus.requireNotNull().onEvent {
        when (it) {
          is PermissionRequests.Notification -> {
            notificationRequester.requireNotNull().requestPermissions()
          }
        }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    stableLayoutHideNavigation()

    val component = ObjectGraph.ApplicationScope.retrieve(this).plusMainComponent().create()
    component.inject(this)
    ObjectGraph.ActivityScope.install(this, component)

    val vm = viewModel.requireNotNull()
    val appName = getString(R.string.app_name)

    setContent {
      val state = vm.state
      val theme by state.theme.collectAsState()

      SaveStateDisposableEffect(vm)

      TrickleTheme(
          theme = theme,
      ) {
        SystemBars(
            theme = theme,
        )
        InstallPYDroidExtras()
        MainEntry(
            modifier = Modifier.fillMaxSize(),
            appName = appName,
            state = state,
            onOpenSettings = { vm.handleOpenSettings() },
            onCloseSettings = { vm.handleCloseSettings() },
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
