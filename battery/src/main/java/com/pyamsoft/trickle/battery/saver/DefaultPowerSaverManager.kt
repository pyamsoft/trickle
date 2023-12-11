package com.pyamsoft.trickle.battery.saver

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
      val resultString =
          when (val result = saver.savePower(enable = enable)) {
            is PowerSaver.State.Disabled -> "DISABLED"
            is PowerSaver.State.Enabled -> "ENABLED"
            is PowerSaver.State.Failure -> "ERROR: ${result.throwable}"
            is PowerSaver.State.NoOp -> "NO-OP ${result.reason}"
          }

      Timber.d { "${saver.name} RESULT: $resultString" }
    }
  }

  override suspend fun reset(): Boolean {
    return savers
        .map { saver -> saver.reset().also { Timber.d { "${saver.name} RESET: $it" } } }
        .any { it }
  }
}
