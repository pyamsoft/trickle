package com.pyamsoft.trickle.home

import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.LocalActivity
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.util.LifecycleEventEffect
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.service.notification.PermissionRequests
import javax.inject.Inject
import kotlin.system.exitProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class HomeInjector : ComposableInjector() {

  @JvmField @Inject internal var viewModel: HomeViewModeler? = null
  @JvmField @Inject internal var permissionRequestBus: EventBus<PermissionRequests>? = null

  override fun onInject(activity: ComponentActivity) {
    ObjectGraph.ActivityScope.retrieve(activity).plusHome().create().inject(this)
  }

  override fun onDispose() {
    viewModel = null
    permissionRequestBus = null
  }
}

@CheckResult
private fun tryIntent(activity: ComponentActivity, intent: Intent): Boolean {
  return try {
    activity.startActivity(intent)
    true
  } catch (e: Throwable) {
    false
  }
}

@CheckResult
private fun safeOpenSettingsIntent(
    activity: ComponentActivity,
    action: String,
): Boolean {

  // Try specific first, may fail on some devices
  val specific = Intent(action, "package:${activity.packageName}".toUri())
  if (!tryIntent(activity, specific)) {
    val generic = Intent(action)
    return tryIntent(activity, generic)
  }

  return true
}

/** On mount hooks */
@Composable
private fun MountHooks(
    viewModeler: HomeViewModeler,
) {
  val scope = rememberCoroutineScope()

  SaveStateDisposableEffect(viewModeler)

  LaunchedEffect(
      viewModeler,
  ) {
    viewModeler.bind(scope = this)
  }

  LifecycleEventEffect(
      event = Lifecycle.Event.ON_RESUME,
  ) {
    viewModeler.handleSync(scope = scope)
  }
}

@Composable
fun HomeEntry(
    modifier: Modifier = Modifier,
    appName: String,
    hasPermission: Boolean,
    onForceBackground: () -> Unit,
) {
  val activity = LocalActivity.current
  val component = rememberComposableInjector { HomeInjector() }
  val viewModel = rememberNotNull(component.viewModel)
  val permissionRequestBus = rememberNotNull(component.permissionRequestBus)

  val scope = rememberCoroutineScope()

  val handleOpenBatterySettings by rememberUpdatedState {
    val a = activity.requireNotNull()
    if (!safeOpenSettingsIntent(a, Settings.ACTION_BATTERY_SAVER_SETTINGS)) {
      if (!safeOpenSettingsIntent(a, Intent.ACTION_POWER_USAGE_SUMMARY)) {
        if (!safeOpenSettingsIntent(a, Settings.ACTION_SETTINGS)) {
          Timber.w { "Could not open any settings pages (battery, power-usage, settings)" }
        }
      }
    }
  }

  val handleDisableBatteryOptimization by rememberUpdatedState {
    val a = activity.requireNotNull()
    if (!safeOpenSettingsIntent(a, Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) {
      Timber.w { "Failed to open Battery optimization settings" }
    }
  }

  val handleCopyToClipboard by rememberUpdatedState { command: String ->
    HomeCopyCommand.copyCommandToClipboard(
        activity.requireNotNull(),
        command,
    )
  }

  MountHooks(
      viewModeler = viewModel,
  )

  HomeScreen(
      modifier = modifier,
      appName = appName,
      state = viewModel,
      hasPermission = hasPermission,
      onForceBackground = onForceBackground,
      onOpenTroubleshooting = { viewModel.handleOpenTroubleshooting() },
      onOpenBatterySettings = { handleOpenBatterySettings() },
      onRestartPowerService = { viewModel.handleRestartClicked(scope = scope) },
      onRestartApp = {
        Timber.d { "APP BEING KILLED FOR ADB RESTART" }
        exitProcess(0)
      },
      onTogglePowerSaving = { viewModel.handleSetPowerSavingEnabled(it) },
      onToggleForceBackground = { viewModel.handleSetForceBackgroundEnabled(it) },
      onDisableBatteryOptimization = { handleDisableBatteryOptimization() },
      onRequestNotificationPermission = {
        // Request permissions
        scope.launch(context = Dispatchers.Default) {
          // See MainActivity
          permissionRequestBus.emit(PermissionRequests.Notification)
        }
      },
      onCopy = { handleCopyToClipboard(it) },
  )
}
