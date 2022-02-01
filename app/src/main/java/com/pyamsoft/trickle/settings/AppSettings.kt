package com.pyamsoft.trickle.settings

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.ui.defaults.DialogDefaults
import com.pyamsoft.pydroid.ui.preference.Preferences
import com.pyamsoft.pydroid.ui.settings.SettingsFragment
import com.pyamsoft.trickle.main.MainComponent
import javax.inject.Inject

internal class AppSettings : SettingsFragment() {

  override val hideClearAll: Boolean = false

  override val hideUpgradeInformation: Boolean = true

  @Inject @JvmField internal var viewModel: SettingsViewModeler? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.obtainFromActivity<MainComponent>(requireActivity())
        .plusAppSettings()
        .create()
        .inject(this)
  }

  @Composable
  override fun customElevation(): Dp {
    return DialogDefaults.DialogElevation
  }

  @Composable
  override fun customBottomItemMargin(): Dp {
    return ZeroSize
  }

  @Composable
  override fun customPostPreferences(): List<Preferences> {
    return emptyList()
  }

  @Composable
  override fun customPrePreferences(): List<Preferences> {
    return emptyList()
  }

  @Composable
  override fun customTopItemMargin(): Dp {
    val state = viewModel.requireNotNull().state()

    val density = LocalDensity.current
    val height = state.topBarOffset
    return remember(density, height) { density.run { height.toDp() } }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    viewModel?.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    viewModel = null
  }

  companion object {

    @JvmStatic
    @CheckResult
    fun newInstance(): Fragment {
      return AppSettings().apply { arguments = Bundle.EMPTY }
    }
  }
}
