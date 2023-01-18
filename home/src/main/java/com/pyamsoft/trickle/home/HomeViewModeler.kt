package com.pyamsoft.trickle.home

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.trickle.process.PowerPreferences
import com.pyamsoft.trickle.process.optimize.BatteryOptimizer
import com.pyamsoft.trickle.process.permission.PermissionChecker
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModeler
@Inject
internal constructor(
    override val state: MutableHomeViewState,
    private val preferences: PowerPreferences,
    private val permissionChecker: PermissionChecker,
    private val batteryOptimizer: BatteryOptimizer,
) : AbstractViewModeler<HomeViewState>(state) {

  // Internal
  private var restartClicks: Int = 0

  private data class LoadConfig(
      var isEnabled: Boolean = false,
      var isIgnore: Boolean = false,
  )

  private fun markLoadCompleted(config: LoadConfig) {
    if (config.isEnabled && config.isIgnore) {
      state.loadingState.value = HomeViewState.LoadingState.DONE
    }
  }

  private fun revealSettingsShortcut() {
    val s = state
    if (restartClicks > RESTART_CLICK_REQUIRED_COUNT) {
      s.isPowerSettingsShortcutVisible.value = true
    }
  }

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> =
      mutableListOf<SaveableStateRegistry.Entry>().apply {
        val s = state

        registry.registerProvider(KEY_CLICKS) { s.isPowerSettingsShortcutVisible.value }
        registry.registerProvider(KEY_IGNORE) { s.isIgnoreInPowerSavingMode.value }
        registry.registerProvider(KEY_PREFERENCE) { s.isPowerSaving.value }
        registry.registerProvider(KEY_PERMISSION) { s.permissionState.value.name }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    val s = state

    registry
        .consumeRestored(KEY_CLICKS)
        ?.let { it as Boolean }
        ?.also { s.isPowerSettingsShortcutVisible.value = it }

    registry
        .consumeRestored(KEY_IGNORE)
        ?.let { it as Boolean }
        ?.also { s.isIgnoreInPowerSavingMode.value = it }

    registry
        .consumeRestored(KEY_PREFERENCE)
        ?.let { it as Boolean }
        ?.also { s.isPowerSaving.value = it }

    registry
        .consumeRestored(KEY_PERMISSION)
        ?.let { it as String }
        ?.let { HomeViewState.PermissionState.valueOf(it) }
        ?.also { s.permissionState.value = it }
  }

  fun beginWatching(
      scope: CoroutineScope,
      onChange: () -> Unit,
  ) {
    val s = state
    if (s.loadingState.value != HomeViewState.LoadingState.NONE) {
      return
    }

    val config = LoadConfig()

    s.loadingState.value = HomeViewState.LoadingState.LOADING
    scope.launch(context = Dispatchers.Main) {
      preferences.observePowerSavingEnabled().collect { ps ->
        state.isPowerSaving.value = ps
        if (s.loadingState.value == HomeViewState.LoadingState.LOADING) {
          config.isEnabled = true
          markLoadCompleted(config)
        }

        onChange()
      }
    }

    scope.launch(context = Dispatchers.Default) {
      preferences.observeIgnoreInPowerSavingMode().collect { ignore ->
        state.isIgnoreInPowerSavingMode.value = ignore
        if (s.loadingState.value == HomeViewState.LoadingState.LOADING) {
          config.isIgnore = true
          markLoadCompleted(config)
        }

        onChange()
      }
    }
  }

  fun handleSync(
      scope: CoroutineScope,
      andThen: () -> Unit,
  ) {
    val s = state
    scope.launch(context = Dispatchers.Main) {
      s.permissionState.value =
          if (permissionChecker.hasSecureSettingsPermission()) HomeViewState.PermissionState.GRANTED
          else HomeViewState.PermissionState.DENIED

      // Battery optimization
      s.isBatteryOptimizationsIgnored.value = batteryOptimizer.isOptimizationsIgnored()

      // Finish
      revealSettingsShortcut()
      andThen()
    }
  }

  fun handleSetPowerSavingEnabled(scope: CoroutineScope, enabled: Boolean) {
    state.isPowerSaving.value = enabled
    scope.launch(context = Dispatchers.Main) { preferences.setPowerSavingEnabled(enabled) }
  }

  fun handleSetIgnoreInPowerSavingMode(scope: CoroutineScope, ignore: Boolean) {
    state.isIgnoreInPowerSavingMode.value = ignore
    scope.launch(context = Dispatchers.Main) { preferences.setIgnoreInPowerSavingMode(ignore) }
  }

  fun handleRestartClicked() {
    restartClicks += 1
    revealSettingsShortcut()
  }

  fun handleOpenTroubleshooting() {
    state.isTroubleshooting.value = true
  }

  companion object {

    private const val RESTART_CLICK_REQUIRED_COUNT = 5

    private const val KEY_CLICKS = "restart_clicks"
    private const val KEY_PERMISSION = "has_permission"
    private const val KEY_PREFERENCE = "preference_enabled"
    private const val KEY_IGNORE = "ignore_power_saving_mode"
  }
}
