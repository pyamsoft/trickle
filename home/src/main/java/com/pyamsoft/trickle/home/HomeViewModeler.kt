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
    state.hasPermission.also { outState.put(KEY_PERMISSION, it) }
    state.isPowerSaving.also { outState.put(KEY_PREFERENCE, it) }
    state.isIgnoreInPowerSavingMode.also { outState.put(KEY_IGNORE, it) }
  }

  override fun restoreState(savedInstanceState: UiSavedStateReader) {
    savedInstanceState.get<Boolean>(KEY_PERMISSION)?.also { state.hasPermission = it }
    savedInstanceState.get<Boolean>(KEY_PREFERENCE)?.also { state.isPowerSaving = it }
    savedInstanceState.get<Boolean>(KEY_IGNORE)?.also { state.isIgnoreInPowerSavingMode = it }
  }

  @CheckResult
  fun listenForPowerSavingChanges(): PreferenceListener {
    return preferences.observerPowerSavingEnabled { enabled -> state.isPowerSaving = enabled }
  }

  @CheckResult
  fun listenForIgnorePowerSavingModeChanges(): PreferenceListener {
    return preferences.observerIgnoreInPowerSavingMode { enabled ->
      state.isIgnoreInPowerSavingMode = enabled
    }
  }

  fun handleSync(
      scope: CoroutineScope,
      andThen: () -> Unit,
  ) {
    scope.launch(context = Dispatchers.Main) {
      state.apply {
        hasPermission = permissionChecker.hasSecureSettingsPermission()
        isPowerSaving = preferences.isPowerSavingEnabled()
        isIgnoreInPowerSavingMode = preferences.isIgnoreInPowerSavingMode()
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

  companion object {

    private const val KEY_PERMISSION = "has_permission"
    private const val KEY_PREFERENCE = "preference_enabled"
    private const val KEY_IGNORE = "ignore_power_saving_mode"
  }
}
