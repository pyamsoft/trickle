package com.pyamsoft.trickle.main

import androidx.compose.runtime.saveable.SaveableStateRegistry
import com.pyamsoft.pydroid.arch.AbstractViewModeler
import com.pyamsoft.trickle.battery.permission.PermissionGuard
import com.pyamsoft.trickle.core.InAppRatingPreferences
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.service.ServiceLauncher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModeler
@Inject
internal constructor(
    override val state: MutableMainViewState,
    private val inAppRatingPreferences: InAppRatingPreferences,
    private val permissionGuard: PermissionGuard,
    private val launcher: ServiceLauncher,
) : MainViewState by state, AbstractViewModeler<MainViewState>(state) {

  override fun registerSaveState(
      registry: SaveableStateRegistry
  ): List<SaveableStateRegistry.Entry> =
      mutableListOf<SaveableStateRegistry.Entry>().apply {
        val s = state

        registry.registerProvider(KEY_IS_SETTINGS_OPEN) { s.isSettingsOpen.value }.also { add(it) }

        registry.registerProvider(KEY_PERMISSION) { s.permission.value.name }
      }

  override fun consumeRestoredState(registry: SaveableStateRegistry) {
    val s = state

    registry
        .consumeRestored(KEY_IS_SETTINGS_OPEN)
        ?.let { it as Boolean }
        ?.also { s.isSettingsOpen.value = it }

    registry
        .consumeRestored(KEY_PERMISSION)
        ?.let { it as String }
        ?.let { MainViewState.PermissionState.valueOf(it) }
        ?.also { s.permission.value = it }
  }

  fun watchForInAppRatingPrompt(
      scope: CoroutineScope,
      onShowInAppRating: () -> Unit,
  ) {
    inAppRatingPreferences
        .listenShowInAppRating()
        .filter { it }
        .distinctUntilChanged()
        .also { f ->
          scope.launch(context = Dispatchers.Default) {
            f.collect { show ->
              if (show) {
                Timber.d { "Show in-app rating" }
                withContext(context = Dispatchers.Main) { onShowInAppRating() }
              }
            }
          }
        }
  }

  fun handleOpenSettings() {
    state.isSettingsOpen.value = true
  }

  fun handleCloseSettings() {
    state.isSettingsOpen.value = false
  }

  fun handleSync(scope: CoroutineScope) {
    scope.launch(context = Dispatchers.Default) {
      // Check if we have permission
      val hasPermission = permissionGuard.canWriteSystemSettings()
      state.permission.value =
          if (hasPermission) MainViewState.PermissionState.GRANTED
          else MainViewState.PermissionState.NOT_GRANTED

      if (!hasPermission) {
        Timber.w { "Stop service without WRITE_SECURE_SETTINGS permission." }
        launcher.stop()
      }
    }
  }

  fun handleAnalyticsMarkOpened() {
    inAppRatingPreferences.markAppOpened()
  }

  companion object {

    private const val KEY_PERMISSION = "has_permission"
    private const val KEY_IS_SETTINGS_OPEN = "is_settings_open"
  }
}
