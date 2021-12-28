package com.pyamsoft.trickle.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.google.accompanist.insets.ProvideWindowInsets
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.pydroid.ui.app.PYDroidActivity
import com.pyamsoft.pydroid.ui.changelog.buildChangeLog
import com.pyamsoft.pydroid.ui.navigator.Navigator
import com.pyamsoft.pydroid.ui.util.dispose
import com.pyamsoft.pydroid.ui.util.recompose
import com.pyamsoft.pydroid.util.stableLayoutHideNavigation
import com.pyamsoft.trickle.R
import com.pyamsoft.trickle.TrickleComponent
import com.pyamsoft.trickle.TrickleTheme
import com.pyamsoft.trickle.databinding.ActivityMainBinding
import javax.inject.Inject
import timber.log.Timber

class MainActivity : PYDroidActivity() {

  override val applicationIcon = R.mipmap.ic_launcher_round

  override val changelog = buildChangeLog {}

  private var injector: MainComponent? = null
  private var viewBinding: ActivityMainBinding? = null

  @JvmField @Inject internal var navigator: Navigator<MainPage>? = null
  @JvmField @Inject internal var viewModel: MainViewModeler? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    // NOTE(Peter):
    // Not full Compose yet
    // Compose has an issue handling Fragments.
    //
    // We need an AndroidView to handle a Fragment, but a Fragment outlives the Activity via the
    // FragmentManager keeping state. The Compose render does not, so when an activity dies from
    // configuration change, the Fragment is headless somewhere in the great beyond. This leads to
    // memory leaks and other issues like Disposable hooks not being called on DisposeEffect blocks.
    // To avoid these growing pains, we use an Activity layout file and then host the ComposeViews
    // from it that are then used to render Activity level views. Fragment transactions happen as
    // normal and then Fragments host ComposeViews too.
    val binding = ActivityMainBinding.inflate(layoutInflater).apply { viewBinding = this }
    setContentView(binding.root)

    injector =
        Injector.obtainFromApplication<TrickleComponent>(this)
            .plusMainComponent()
            .create(
                this,
                binding.mainFragmentContainerView.id,
            )
            .also { c -> c.inject(this) }
    setTheme(R.style.Theme_Trickle)
    super.onCreate(savedInstanceState)
    stableLayoutHideNavigation()

    // Snackbar respects window offsets and hosts snackbar composables
    // Because these are not in a nice Scaffold, we cannot take advantage of Coordinator style
    // actions (a FAB will not move out of the way for example)
    val navi = navigator.requireNotNull()
    val vm = viewModel.requireNotNull()

    vm.restoreState(savedInstanceState)

    binding.mainComposeBottom.setContent {
      vm.Render { state ->
        val theme = state.theme
        val snackbarHostState = remember { SnackbarHostState() }

        SystemBars(theme)
        TrickleTheme(theme) {
          ProvideWindowInsets {

            // Need to have box or snackbars push up bottom bar
            Box(
                contentAlignment = Alignment.BottomCenter,
            ) {
              RatingScreen(
                  snackbarHostState = snackbarHostState,
              )
              VersionCheckScreen(
                  snackbarHostState = snackbarHostState,
              )
            }
          }
        }
      }
    }

    vm.handleSyncDarkTheme(this)

    navi.restore {
      if (it.select(MainPage.Home.asScreen())) {
        Timber.d("Loaded default Home screen")
      }
    }
  }

  override fun onBackPressed() {
    onBackPressedDispatcher.also { dispatcher ->
      if (dispatcher.hasEnabledCallbacks()) {
        dispatcher.onBackPressed()
      } else {
        super.onBackPressed()
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    viewModel?.handleSyncDarkTheme(this)
    viewBinding?.apply { mainComposeBottom.recompose() }
  }

  override fun getSystemService(name: String): Any? {
    return when (name) {
      // Must be defined before super.onCreate or throws npe
      MainComponent::class.java.name -> injector.requireNotNull()
      else -> super.getSystemService(name)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    viewBinding?.apply { this.mainComposeBottom.dispose() }
    viewBinding = null

    navigator = null
    viewModel = null
    injector = null
  }
}
