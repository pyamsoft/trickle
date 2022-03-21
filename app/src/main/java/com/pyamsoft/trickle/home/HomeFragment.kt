/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.trickle.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ViewWindowInsetObserver
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.trickle.R
import com.pyamsoft.trickle.TrickleTheme
import com.pyamsoft.trickle.main.MainComponent
import com.pyamsoft.trickle.process.work.PowerSaver
import com.pyamsoft.trickle.service.ServiceLauncher
import com.pyamsoft.trickle.settings.SettingsDialog
import javax.inject.Inject
import kotlin.system.exitProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : Fragment() {

  @JvmField @Inject internal var theming: Theming? = null
  @JvmField @Inject internal var viewModel: HomeViewModeler? = null
  @JvmField @Inject internal var powerSaver: PowerSaver? = null
  @JvmField @Inject internal var launcher: ServiceLauncher? = null

  private var windowInsetObserver: ViewWindowInsetObserver? = null

  @CheckResult
  private fun tryOpenIntent(intent: Intent): Boolean {
    return try {
      requireActivity().startActivity(intent)
      true
    } catch (e: ActivityNotFoundException) {
      Timber.e(e, "Could not open intent: ${intent.action}")
      false
    }
  }

  private fun handleOpenApplicationSettings() {
    SettingsDialog.show(requireActivity())
  }

  private fun handleOpenSystemSettings() {
    if (!tryOpenIntent(BATTERY_INTENT)) {
      if (!tryOpenIntent(POWER_USAGE_INTENT)) {
        if (!tryOpenIntent(SETTINGS_INTENT)) {
          Timber.w("Could not open any settings pages (battery, power-usage, settings)")
        }
      }
    }
  }

  private fun handleRestartApp() {
    Timber.d("APP BEING KILLED FOR ADB RESTART")
    exitProcess(0)
  }

  private fun handleLaunchService() {
    viewLifecycleOwner.lifecycleScope.launch(context = Dispatchers.Main) {
      launcher.requireNotNull().launch()
    }
  }

  private fun handleSyncPermissionState() {
    viewModel.requireNotNull().handleSync(scope = viewLifecycleOwner.lifecycleScope) {
      handleLaunchService()
    }
  }

  private fun handleRestartPowerService() {
    viewLifecycleOwner.lifecycleScope.launch(context = Dispatchers.Main) {
      viewModel.requireNotNull().handleRestartClicked()
      if (powerSaver.requireNotNull().forcePowerSaving(enable = false)) {
        Timber.d("Power saving mode: OFF")
      } else {
        Timber.w("Did not modify power saving mode")
      }
    }
  }

  private fun handleCopyCommand(command: String) {
    HomeCopyCommand.copyCommandToClipboard(
        requireActivity(),
        "ADB Command",
        command,
    )
  }

  private fun handleTogglePowerSaving(enabled: Boolean) {
    viewModel.requireNotNull().handleSetPowerSavingEnabled(
            scope = viewLifecycleOwner.lifecycleScope,
            enabled = enabled,
        ) { handleLaunchService() }
  }

  private fun handleToggleIgnoreInPowerSavingMode(ignore: Boolean) {
    viewModel.requireNotNull().handleSetIgnoreInPowerSavingMode(
            scope = viewLifecycleOwner.lifecycleScope,
            ignore = ignore,
        ) { handleLaunchService() }
  }

  private fun handleToggleExitWhileCharging(exit: Boolean) {
    viewModel.requireNotNull().handleSetExitwhileCharging(
            scope = viewLifecycleOwner.lifecycleScope,
            exit = exit,
        ) { handleLaunchService() }
  }

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()
    Injector.obtainFromActivity<MainComponent>(act).plusHome().create().inject(this)

    val themeProvider = ThemeProvider { theming.requireNotNull().isDarkTheme(act) }
    val vm = viewModel.requireNotNull()
    return ComposeView(act).apply {
      id = R.id.screen_home

      val observer = ViewWindowInsetObserver(this)
      val windowInsets = observer.start()
      windowInsetObserver = observer

      setContent {
        vm.Render { state ->
          act.TrickleTheme(themeProvider) {
            CompositionLocalProvider(LocalWindowInsets provides windowInsets) {
              HomeScreen(
                  modifier = Modifier.fillMaxSize(),
                  state = state,
                  appNameRes = R.string.app_name,
                  onCopy = { handleCopyCommand(it) },
                  onOpenBatterySettings = { handleOpenSystemSettings() },
                  onOpenApplicationSettings = { handleOpenApplicationSettings() },
                  onRestartPowerService = { handleRestartPowerService() },
                  onRestartApp = { handleRestartApp() },
                  onTogglePowerSaving = { handleTogglePowerSaving(it) },
                  onToggleIgnoreInPowerSavingMode = { handleToggleIgnoreInPowerSavingMode(it) },
                  onToggleExitWhileCharging = { handleToggleExitWhileCharging(it) },
              )
            }
          }
        }
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.requireNotNull().also { vm ->
      vm.restoreState(savedInstanceState)

      // Listen for changes in preferences
      vm.listenForExitWhileChargingChanges().also { viewLifecycleOwner.doOnDestroy { it.cancel() } }
      vm.listenForPowerSavingChanges().also { viewLifecycleOwner.doOnDestroy { it.cancel() } }
      vm.listenForIgnorePowerSavingModeChanges().also {
        viewLifecycleOwner.doOnDestroy { it.cancel() }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    handleSyncPermissionState()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    recompose()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    dispose()

    windowInsetObserver?.stop()
    windowInsetObserver = null

    theming = null
    viewModel = null
    powerSaver = null
  }

  companion object {

    private val BATTERY_INTENT = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
    private val POWER_USAGE_INTENT = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
    private val SETTINGS_INTENT = Intent(Settings.ACTION_SETTINGS)

    @JvmStatic
    @CheckResult
    fun newInstance(): Fragment {
      return HomeFragment().apply { arguments = Bundle.EMPTY }
    }
  }
}
