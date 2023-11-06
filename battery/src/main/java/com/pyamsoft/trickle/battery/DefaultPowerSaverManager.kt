package com.pyamsoft.trickle.battery

import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultPowerSaverManager
@Inject
internal constructor(
    private val savers: MutableSet<@JvmSuppressWildcards PowerSaver>,
) : PowerSaverManager {

  override suspend fun runPowerSavers(enable: Boolean) {
    savers.forEach { saver ->
      val result = saver.setSystemPowerSaving(enable = enable)
      Timber.d { "${saver.name} RESULT: $result" }
    }
  }

  override suspend fun resetSystemPowerSavingState(): Boolean {
    return savers
        .map { saver ->
          saver.resetSystemPowerSavingState().also { Timber.d { "${saver.name} RESET: $it" } }
        }
        .any { it }
  }
}
