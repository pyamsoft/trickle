package com.pyamsoft.trickle.main

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pyamsoft.pydroid.arch.SaveStateDisposableEffect
import com.pyamsoft.pydroid.ui.inject.ComposableInjector
import com.pyamsoft.pydroid.ui.inject.rememberComposableInjector
import com.pyamsoft.pydroid.ui.util.LifecycleEventEffect
import com.pyamsoft.pydroid.ui.util.fillUpToPortraitHeight
import com.pyamsoft.pydroid.ui.util.rememberNotNull
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.settings.SettingsDialog
import com.pyamsoft.trickle.ui.LANDSCAPE_MAX_WIDTH
import javax.inject.Inject

internal class MainInjector @Inject internal constructor() : ComposableInjector() {

  @JvmField @Inject internal var viewModel: MainViewModeler? = null

  override fun onInject(activity: ComponentActivity) {
    ObjectGraph.ActivityScope.retrieve(activity).inject(this)
  }

  override fun onDispose() {
    viewModel = null
  }
}

@Composable
private fun MountHooks(
    viewModel: MainViewModeler,
    onShowInAppRating: () -> Unit,
) {
  val scope = rememberCoroutineScope()

  val handleShowInAppRating by rememberUpdatedState(onShowInAppRating)

  SaveStateDisposableEffect(viewModel)

  LaunchedEffect(
      viewModel,
  ) {
    viewModel.watchForInAppRatingPrompt(
        scope = this,
        onShowInAppRating = { handleShowInAppRating() },
    )
  }

  LifecycleEventEffect(
      event = Lifecycle.Event.ON_START,
  ) {
    viewModel.handleAnalyticsMarkOpened()
  }

  LifecycleEventEffect(
      event = Lifecycle.Event.ON_RESUME,
  ) {
    viewModel.handleSync(scope = scope)
  }
}

@Composable
fun MainEntry(
    modifier: Modifier = Modifier,
    appName: String,
    onShowInAppRating: () -> Unit,
) {
  val component = rememberComposableInjector { MainInjector() }
  val viewModel = rememberNotNull(component.viewModel)

  val handleShowInAppRating by rememberUpdatedState(onShowInAppRating)

  MountHooks(
      viewModel = viewModel,
      onShowInAppRating = { handleShowInAppRating() },
  )

  MainScreen(
      modifier = modifier.fillMaxSize(),
      appName = appName,
      state = viewModel,
      onOpenSettings = { viewModel.handleOpenSettings() },
  )

  val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
  if (isSettingsOpen) {
    SettingsDialog(
        modifier =
            Modifier.fillUpToPortraitHeight()
                .widthIn(
                    max = LANDSCAPE_MAX_WIDTH,
                ),
        onDismiss = { viewModel.handleCloseSettings() },
    )
  }
}
