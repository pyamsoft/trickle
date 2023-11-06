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

  override suspend fun savePower(enable: Boolean) {
    savers.forEach { saver ->
      val result = saver.savePower(enable = enable)
      Timber.d { "${saver.name} RESULT: $result" }
    }
  }

  override suspend fun reset(): Boolean {
    return savers
        .map { saver -> saver.reset().also { Timber.d { "${saver.name} RESET: $it" } } }
        .any { it }
  }
}
