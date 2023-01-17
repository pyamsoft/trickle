package com.pyamsoft.trickle.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.notify.NotifyGuard
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.util.LifecycleEffect
import com.pyamsoft.pydroid.ui.util.rememberActivity
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.process.work.PowerSaver
import com.pyamsoft.trickle.service.NotificationRefreshEvent
import com.pyamsoft.trickle.service.ServiceLauncher
import com.pyamsoft.trickle.settings.SettingsDialog
import javax.inject.Inject
import kotlin.system.exitProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

private val BATTERY_INTENT = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
private val POWER_USAGE_INTENT = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
private val SETTINGS_INTENT = Intent(Settings.ACTION_SETTINGS)

internal class HomeInjector : ComposableInjector() {

  @JvmField @Inject internal var viewModel: HomeViewModeler? = null
  @JvmField @Inject internal var powerSaver: PowerSaver? = null
  @JvmField @Inject internal var launcher: ServiceLauncher? = null
  @JvmField @Inject internal var notifyGuard: NotifyGuard? = null
  @JvmField @Inject internal var notificationRefreshBus: EventBus<NotificationRefreshEvent>? = null
  @JvmField @Inject internal var permissionRequestBus: EventBus<PermissionRequests>? = null
  @JvmField @Inject internal var permissionResponseBus: EventBus<PermissionResponse>? = null

  override fun onInject(activity: FragmentActivity) {
    ObjectGraph.ActivityScope.retrieve(activity).plusHome().create().inject(this)
  }

  override fun onDispose() {
    viewModel = null
    powerSaver = null
    launcher = null
    notifyGuard = null
    notificationRefreshBus = null
    permissionRequestBus = null
    permissionResponseBus = null
  }
}

private data class MountHookResults(
    val notificationState: State<Boolean>,
)

/** Sets up permission request interaction */
@Composable
private fun RegisterPermissionRequests(
    notificationPermissionState: MutableState<Boolean>,
    permissionResponseBus: EventBus<PermissionResponse>,
    notificationRefreshBus: EventBus<NotificationRefreshEvent>,
) {
  LaunchedEffect(
      permissionResponseBus,
      notificationRefreshBus,
      notificationPermissionState,
  ) {
    val scope = this
    scope.launch(context = Dispatchers.Main) {

      // See MainActivity
      permissionResponseBus.onEvent { resp ->
        when (resp) {
          is PermissionResponse.RefreshNotification -> {
            // Update state variable
            notificationPermissionState.value = true
            notificationRefreshBus.send(NotificationRefreshEvent)
          }
        }
      }
    }
  }
}

/** On mount hooks */
@Composable
@CheckResult
private fun mountHooks(
    component: HomeInjector,
    onLaunchService: () -> Unit,
    onSyncPermissionState: () -> Unit,
): MountHookResults {
  val viewModel = rememberNotNull(component.viewModel)
  val notifyGuard = rememberNotNull(component.notifyGuard)
  val permissionResponseBus = rememberNotNull(component.permissionResponseBus)
  val notificationRefreshBus = rememberNotNull(component.notificationRefreshBus)

  val notificationState = remember { mutableStateOf(notifyGuard.canPostNotification()) }

  // As early as possible because of Lifecycle quirks
  RegisterPermissionRequests(
      notificationPermissionState = notificationState,
      notificationRefreshBus = notificationRefreshBus,
      permissionResponseBus = permissionResponseBus,
  )

  val owner = LocalLifecycleOwner.current
  val activity = rememberActivity()

  val handleLaunchService by rememberUpdatedState(onLaunchService)
  val handleSyncPermissionState by rememberUpdatedState(onSyncPermissionState)

  LaunchedEffect(
      viewModel,
      owner,
  ) {
    viewModel.beginWatching(scope = owner.lifecycleScope) { handleLaunchService() }
  }

  LifecycleEffect {
    object : DefaultLifecycleObserver {

      override fun onResume(owner: LifecycleOwner) {
        handleSyncPermissionState()
        activity.reportFullyDrawn()
      }
    }
  }

  return remember(
      notificationState,
  ) {
    MountHookResults(
        notificationState = notificationState,
    )
  }
}

@Composable
fun HomeEntry(
    modifier: Modifier = Modifier,
    appName: String,
) {
  val component = rememberComposableInjector { HomeInjector() }
  val viewModel = rememberNotNull(component.viewModel)
  val launcher = rememberNotNull(component.launcher)
  val powerSaver = rememberNotNull(component.powerSaver)
  val permissionRequestBus = rememberNotNull(component.permissionRequestBus)

  val activity = rememberActivity()
  val scope = rememberCoroutineScope()

  // Since our mount hooks use this callback in bind, we must declare it first
  val handleLaunchService by rememberUpdatedState {
    scope.launch(context = Dispatchers.Main) {
      Timber.d("Launch/Refresh Power service")
      launcher.launch()
    }

    return@rememberUpdatedState
  }

  val handleSyncPermissionState by rememberUpdatedState {
    viewModel.handleSync(scope = scope) {
      // Once synced, attempt service refresh
      handleLaunchService()
    }
  }

  // Hooks that run on mount
  val hooks =
      mountHooks(
          component = component,
          onLaunchService = handleLaunchService,
          onSyncPermissionState = handleSyncPermissionState,
      )

  val notificationState = hooks.notificationState

  val tryOpenIntent by rememberUpdatedState { intent: Intent ->
    return@rememberUpdatedState try {
      activity.startActivity(intent)
      true
    } catch (e: ActivityNotFoundException) {
      Timber.e(e, "Could not open intent: ${intent.action}")
      false
    }
  }

  val safeOpenSettingsIntent by rememberUpdatedState { action: String ->
    // Try specific first, may fail on some devices
    var intent = Intent(action, "package:${activity.packageName}".toUri())
    if (!tryOpenIntent(intent)) {
      Timber.w("Failed specific intent for $action")
      intent = Intent(action)
      if (!tryOpenIntent(intent)) {
        Timber.w("Failed generic intent for $action")
        return@rememberUpdatedState false
      }
    }

    return@rememberUpdatedState true
  }

  val handleOpenBatterySettings by rememberUpdatedState {
    if (!safeOpenSettingsIntent(
        Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS,
    )) {
      Timber.w("Failed to open Battery optimization settings")
    }
  }

  val handleOpenSystemSettings by rememberUpdatedState {
    if (!tryOpenIntent(BATTERY_INTENT)) {
      if (!tryOpenIntent(POWER_USAGE_INTENT)) {
        if (!tryOpenIntent(SETTINGS_INTENT)) {
          Timber.w("Could not open any settings pages (battery, power-usage, settings)")
        }
      }
    }
  }

  val handleRestartApp by rememberUpdatedState {
    Timber.d("APP BEING KILLED FOR ADB RESTART")
    exitProcess(0)
  }

  val handleRestartPowerService by rememberUpdatedState {
    scope.launch(context = Dispatchers.Main) {
      viewModel.handleRestartClicked()
      when (val result = powerSaver.powerSaveModeOff()) {
        is PowerSaver.State.Disabled -> Timber.d("Power Saving DISABLED")
        is PowerSaver.State.Enabled -> Timber.d("Power Saving ENABLED")
        is PowerSaver.State.Failure -> Timber.w(result.throwable, "Power Saving Error")
      }
    }

    return@rememberUpdatedState
  }

  val handleCopyCommand by rememberUpdatedState { command: String ->
    HomeCopyCommand.copyCommandToClipboard(
        activity,
        command,
    )
  }

  val handleTogglePowerSaving by rememberUpdatedState { enabled: Boolean ->
    viewModel.handleSetPowerSavingEnabled(
        scope = scope,
        enabled = enabled,
    )
  }

  val handleToggleIgnoreInPowerSavingMode by rememberUpdatedState { ignore: Boolean ->
    viewModel.handleSetIgnoreInPowerSavingMode(
        scope = scope,
        ignore = ignore,
    )
  }

  val handleRequestNotificationPermission by rememberUpdatedState {
    scope.launch(context = Dispatchers.IO) {
      // See MainActivity
      permissionRequestBus.send(PermissionRequests.Notification)
    }

    return@rememberUpdatedState
  }

  val (showDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }
  val handleDismissSettings by rememberUpdatedState { setShowDialog(false) }
  val handleShowSettings by rememberUpdatedState { setShowDialog(true) }

  HomeScreen(
      modifier = modifier,
      state = viewModel.state(),
      appName = appName,
      hasNotificationPermission = notificationState.value,
      onCopy = handleCopyCommand,
      onOpenBatterySettings = handleOpenSystemSettings,
      onOpenApplicationSettings = handleShowSettings,
      onRestartPowerService = handleRestartPowerService,
      onRestartApp = handleRestartApp,
      onTogglePowerSaving = handleTogglePowerSaving,
      onToggleIgnoreInPowerSavingMode = handleToggleIgnoreInPowerSavingMode,
      onDisableBatteryOptimization = handleOpenBatterySettings,
      onRequestNotificationPermission = handleRequestNotificationPermission,
  )

  if (showDialog) {
    SettingsDialog(
        onDismiss = handleDismissSettings,
    )
  }
}
