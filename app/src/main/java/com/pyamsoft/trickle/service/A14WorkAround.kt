package com.pyamsoft.trickle.service

import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pyamsoft.pydroid.util.doOnDestroy
import kotlinx.coroutines.CoroutineScope

internal interface A14WorkAround {

  /**
   * On Android 14, we sometimes get into a state where we are still alive and service is running
   * but we can't actually receive Screen state intents probably due to system changes in A14. We
   * can, for some reason though, still receive Activity callbacks.
   *
   * Register on the DisplayManager and watch for the display state to change.
   */
  @CheckResult fun register(scope: CoroutineScope): Unregister

  fun interface Unregister {
    fun unregister()
  }
}

internal fun A14WorkAround.registerToLifecycle(owner: LifecycleOwner) {
  val unregister = register(scope = owner.lifecycleScope)
  owner.doOnDestroy { unregister.unregister() }
}
