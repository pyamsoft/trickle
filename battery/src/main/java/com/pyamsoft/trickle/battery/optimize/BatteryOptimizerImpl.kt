package com.pyamsoft.trickle.battery.optimize

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.requireNotNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
internal class BatteryOptimizerImpl
@Inject
internal constructor(
    private val context: Context,
) : BatteryOptimizer {

  private val powerManager by lazy { context.getSystemService<PowerManager>().requireNotNull() }

  override suspend fun isOptimizationsIgnored(): Boolean =
      withContext(context = Dispatchers.Main) {
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
      }

  override suspend fun isInPowerSavingMode(): Boolean =
      withContext(context = Dispatchers.Main) { powerManager.isPowerSaveMode }
}
