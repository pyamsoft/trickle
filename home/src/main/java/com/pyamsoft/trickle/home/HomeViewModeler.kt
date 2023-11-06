package com.pyamsoft.trickle.home

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.notify.NotifyGuard
import com.pyamsoft.trickle.battery.PowerPreferences
import com.pyamsoft.trickle.battery.PowerSaver
import com.pyamsoft.trickle.battery.PowerSaverManager
import com.pyamsoft.trickle.battery.optimize.BatteryOptimizer
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.service.ServiceLauncher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModeler
@Inject
internal constructor(
    override val state: MutableHomeViewState,
    private val preferences: PowerPreferences,
    private val batteryOptimizer: BatteryOptimizer,
    private val notifyGuard: NotifyGuard,
    private val launcher: ServiceLauncher,
    private val saverManager: PowerSaverManager,
) : HomeViewState by state, AbstractViewModeler<HomeViewState>(state) {

  private val restartClicks = MutableStateFlow(0)

  private data class LoadConfig(
      var isEnabled: Boolean = false,
  )

  private fun markLoadCompleted(config: LoadConfig) {
    if (config.isEnabled) {
      state.loadingState.value = HomeViewState.LoadingState.DONE

      // Launch now that we are loaded
      // IF we launch before being load complete, then the
      // value of state.isPowerSaving is inaccurate
      launchPowerService(state.isPowerSaving.value)
    }
  }

  private fun launchPowerService(enabled: Boolean) {
    if (enabled) {
      launcher.start()
    } else {
      launcher.stop()
    }
  }

  private fun onPowerEnabledChanged(enabled: Boolean) {
    state.isPowerSaving.value = enabled
    launchPowerService(enabled)
  }

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> =
      mutableListOf<SaveableStateRegistry.Entry>().apply {
        val s = state

        registry.registerProvider(KEY_CLICKS) { s.isPowerSettingsShortcutVisible.value }
        registry.registerProvider(KEY_PREFERENCE) { s.isPowerSaving.value }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    val s = state

    registry
        .consumeRestored(KEY_CLICKS)
        ?.let { it as Boolean }
        ?.also { s.isPowerSettingsShortcutVisible.value = it }

    registry
        .consumeRestored(KEY_PREFERENCE)
        ?.let { it as Boolean }
        ?.also { s.isPowerSaving.value = it }
  }

  fun bind(scope: CoroutineScope) {
    val s = state
    if (s.loadingState.value != HomeViewState.LoadingState.NONE) {
      return
    }

    val config = LoadConfig()

    s.loadingState.value = HomeViewState.LoadingState.LOADING
    scope.launch(context = Dispatchers.Main) {
      preferences.observePowerSavingEnabled().collect { ps ->
        onPowerEnabledChanged(ps)
        if (s.loadingState.value == HomeViewState.LoadingState.LOADING) {
          config.isEnabled = true
          markLoadCompleted(config)
        }
      }
    }

    restartClicks.also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { clicks ->
          state.isPowerSettingsShortcutVisible.value = clicks >= RESTART_CLICK_REQUIRED_COUNT
        }
      }
    }
  }

  fun handleSync(scope: CoroutineScope) {
    val s = state
    scope.launch(context = Dispatchers.Default) {
      // Battery optimization
      s.isBatteryOptimizationsIgnored.value = batteryOptimizer.isOptimizationsIgnored()

      // Notifications
      s.hasNotificationPermission.value = notifyGuard.canPostNotification()

      // Launch the power service once we are loaded
      // If we are not loaded, then s.isPowerSaving is inaccurate.
      if (s.loadingState.value == HomeViewState.LoadingState.DONE) {
        launchPowerService(s.isPowerSaving.value)
      }
    }
  }

  fun handleSetPowerSavingEnabled(enabled: Boolean) {
    state.isPowerSaving.value = enabled
    preferences.setPowerSavingEnabled(enabled)
  }

  fun handleRestartClicked(scope: CoroutineScope) {
    restartClicks.update { it + 1 }
    scope.launch(context = Dispatchers.Default) {
      if (saverManager.resetSystemPowerSavingState()) {
        Timber.d { "Power Setting Reset!" }
      }
    }
  }

  fun handleOpenTroubleshooting() {
    state.isTroubleshooting.value = true
  }

  companion object {

    private const val RESTART_CLICK_REQUIRED_COUNT = 5

    private const val KEY_CLICKS = "restart_clicks"
    private const val KEY_PREFERENCE = "preference_enabled"
  }
}
