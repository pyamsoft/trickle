package com.pyamsoft.trickle.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.app.makeFullscreen
import com.pyamsoft.pydroid.ui.navigator.Navigator
import com.pyamsoft.pydroid.ui.theme.ThemeProvider
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.ui.util.show
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.TrickleTheme
import com.pyamsoft.trickle.databinding.DialogSettingsBinding
import javax.inject.Inject

class SettingsDialog : AppCompatDialogFragment() {

  @Inject @JvmField internal var navigator: Navigator<SettingsPage>? = null
  @Inject @JvmField internal var theming: Theming? = null
  @Inject @JvmField internal var viewModel: SettingsViewModeler? = null

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View {
    val act = requireActivity()
    val binding = DialogSettingsBinding.inflate(inflater, container, false)

    ObjectGraph.ActivityScope.retrieve(act)
        .plusSettings()
        .create(this, binding.dialogSettings.id)
        .inject(this)

    val vm = viewModel.requireNotNull()
    val themeProvider = ThemeProvider { theming.requireNotNull().isDarkTheme(act) }

    binding.dialogComposeTop.setContent {
      val handleTopBarHeightChanged by rememberUpdatedState { size: IntSize ->
        vm.handleTopBarHeight(size.height)
      }

      val handleDismissed by rememberUpdatedState { dismiss() }

      act.TrickleTheme(themeProvider) {
        SettingsToolbar(
            modifier = Modifier.fillMaxWidth().onSizeChanged(handleTopBarHeightChanged),
            onClose = handleDismissed,
        )
      }
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    makeFullscreen()

    viewModel.requireNotNull().restoreState(savedInstanceState)

    navigator.requireNotNull().also { n ->
      n.restoreState(savedInstanceState)
      n.loadIfEmpty { SettingsPage.Settings }
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    makeFullscreen()
    recompose()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    viewModel?.saveState(outState)
    navigator?.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    dispose()

    navigator = null
    theming = null
    viewModel = null
  }

  companion object {

    private val TAG = SettingsDialog::class.java.name

    @JvmStatic
    @CheckResult
    private fun newInstance(): DialogFragment {
      return SettingsDialog().apply { arguments = Bundle.EMPTY }
    }

    @JvmStatic
    fun show(activity: FragmentActivity) {
      return newInstance().show(activity, TAG)
    }
  }
}
