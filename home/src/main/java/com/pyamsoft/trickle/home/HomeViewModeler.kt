package com.pyamsoft.trickle.home

import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.pydroid.arch.UiSavedStateReader
import com.pyamsoft.pydroid.arch.UiSavedStateWriter
import com.pyamsoft.pydroid.util.PreferenceListener
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

  private fun revealSettingsShortcut() {
    val s = state
    if (s.restartClicks > RESTART_CLICK_REQUIRED_COUNT) {
      s.isPowerSettingsShortcutVisible = true
    }
  }

  @CheckResult
  fun listenForPowerSavingChanges(): PreferenceListener {
    return preferences.observePowerSavingEnabled { state.isPowerSaving = it }
  }

  @CheckResult
  fun listenForIgnorePowerSavingModeChanges(): PreferenceListener {
    return preferences.observeIgnoreInPowerSavingMode { state.isIgnoreInPowerSavingMode = it }
  }

  @CheckResult
  fun listenForExitWhileChargingChanges(): PreferenceListener {
    return preferences.observeExitPowerSavingModeWhileCharging { state.isExitWhileCharging = it }
  }

  fun handleSync(
      scope: CoroutineScope,
      andThen: () -> Unit,
  ) {
    val s = state
    s.loading = true
    scope.launch(context = Dispatchers.Main) {
      s.apply {
        hasPermission = permissionChecker.hasSecureSettingsPermission()
        isPowerSaving = preferences.isPowerSavingEnabled()
        isIgnoreInPowerSavingMode = preferences.isIgnoreInPowerSavingMode()
        isExitWhileCharging = preferences.isExitPowerSavingModeWhileCharging()

        revealSettingsShortcut()

        loading = false
      }

      andThen()
    }
  }

  fun handleSetPowerSavingEnabled(
      scope: CoroutineScope,
      enabled: Boolean,
      andThen: () -> Unit,
  ) {
    state.isPowerSaving = enabled
    scope.launch(context = Dispatchers.Main) {
      preferences.setPowerSavingEnabled(enabled)
      andThen()
    }
  }

  fun handleSetIgnoreInPowerSavingMode(
      scope: CoroutineScope,
      ignore: Boolean,
      andThen: () -> Unit,
  ) {
    state.isIgnoreInPowerSavingMode = ignore
    scope.launch(context = Dispatchers.Main) {
      preferences.setIgnoreInPowerSavingMode(ignore)
      andThen()
    }
  }

  fun handleRestartClicked() {
    val s = state
    s.restartClicks += 1
    revealSettingsShortcut()
  }

  fun handleSetExitwhileCharging(scope: CoroutineScope, exit: Boolean, andThen: () -> Unit) {
    state.isExitWhileCharging = exit
    scope.launch(context = Dispatchers.Main) {
      preferences.setExitPowerSavingModeWhileCharging(exit)
      andThen()
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
