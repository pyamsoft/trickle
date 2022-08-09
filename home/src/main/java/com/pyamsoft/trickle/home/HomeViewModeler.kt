package com.pyamsoft.trickle.home

import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.arch.UiSavedStateReader
import com.pyamsoft.pydroid.arch.UiSavedStateWriter
import com.pyamsoft.trickle.process.PowerPreferences
import com.pyamsoft.trickle.process.permission.PermissionChecker
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModeler
@Inject
internal constructor(
    private val state: MutableHomeViewState,
    private val preferences: PowerPreferences,
    private val permissionChecker: PermissionChecker,
) : AbstractViewModeler<HomeViewState>(state) {

  private data class LoadConfig(
      var isEnabled: Boolean,
      var isIgnore: Boolean,
      var isExit: Boolean,
  )

  private fun markLoadCompleted(config: LoadConfig) {
    if (config.isEnabled && config.isIgnore && config.isExit) {
      state.loading = false
    }
  }

  private fun revealSettingsShortcut() {
    val s = state
    if (s.restartClicks > RESTART_CLICK_REQUIRED_COUNT) {
      s.isPowerSettingsShortcutVisible = true
    }
  }

  override fun saveState(outState: UiSavedStateWriter) {
    state.apply {
      hasPermission.also { outState.put(KEY_PERMISSION, it) }
      isPowerSaving.also { outState.put(KEY_PREFERENCE, it) }
      isIgnoreInPowerSavingMode.also { outState.put(KEY_IGNORE, it) }
      isPowerSettingsShortcutVisible.also { outState.put(KEY_CLICKS, it) }
      isExitWhileCharging.also { outState.put(KEY_EXIT, it) }
    }
  }

  override fun restoreState(savedInstanceState: UiSavedStateReader) {
    val s = state
    savedInstanceState.apply {
      get<Boolean>(KEY_PERMISSION)?.also { s.hasPermission = it }
      get<Boolean>(KEY_PREFERENCE)?.also { s.isPowerSaving = it }
      get<Boolean>(KEY_IGNORE)?.also { s.isIgnoreInPowerSavingMode = it }
      get<Boolean>(KEY_CLICKS)?.also { s.isPowerSettingsShortcutVisible = it }
      get<Boolean>(KEY_EXIT)?.also { s.isExitWhileCharging = it }
    }
  }

  fun beginWatching(
      scope: CoroutineScope,
      onChange: () -> Unit,
  ) {
    val s = state
    s.loading = true

    val config =
        LoadConfig(
            isEnabled = false,
            isIgnore = false,
            isExit = false,
        )

    scope.launch(context = Dispatchers.Main) {
      preferences.observePowerSavingEnabled().collect { ps ->
        state.isPowerSaving = ps
        if (s.loading) {
          config.isEnabled = true
          markLoadCompleted(config)
        }

        onChange()
      }
    }

    scope.launch(context = Dispatchers.Default) {
      preferences.observeIgnoreInPowerSavingMode().collect { ignore ->
        state.isIgnoreInPowerSavingMode = ignore
        if (s.loading) {
          config.isIgnore = true
          markLoadCompleted(config)
        }

        onChange()
      }
    }

    scope.launch(context = Dispatchers.Default) {
      preferences.observeExitPowerSavingModeWhileCharging().collect { exit ->
        state.isExitWhileCharging = exit
        if (s.loading) {
          config.isExit = true
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
    scope.launch(context = Dispatchers.Main) {
      state.hasPermission = permissionChecker.hasSecureSettingsPermission()
    }

    revealSettingsShortcut()
    andThen()
  }

  fun handleSetPowerSavingEnabled(scope: CoroutineScope, enabled: Boolean) {
    state.isPowerSaving = enabled
    scope.launch(context = Dispatchers.Main) { preferences.setPowerSavingEnabled(enabled) }
  }

  fun handleSetIgnoreInPowerSavingMode(scope: CoroutineScope, ignore: Boolean) {
    state.isIgnoreInPowerSavingMode = ignore
    scope.launch(context = Dispatchers.Main) { preferences.setIgnoreInPowerSavingMode(ignore) }
  }

  fun handleRestartClicked() {
    val s = state
    s.restartClicks += 1
    revealSettingsShortcut()
  }

  fun handleSetExitwhileCharging(scope: CoroutineScope, exit: Boolean) {
    state.isExitWhileCharging = exit
    scope.launch(context = Dispatchers.Main) {
      preferences.setExitPowerSavingModeWhileCharging(exit)
    }
  }

  companion object {

    private const val RESTART_CLICK_REQUIRED_COUNT = 5

    private const val KEY_CLICKS = "restart_clicks"
    private const val KEY_EXIT = "exit_charging"
    private const val KEY_PERMISSION = "has_permission"
    private const val KEY_PREFERENCE = "preference_enabled"
    private const val KEY_IGNORE = "ignore_power_saving_mode"
  }
}
