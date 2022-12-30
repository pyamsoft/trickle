package com.pyamsoft.trickle.preference

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.util.booleanFlow
import com.pyamsoft.trickle.process.PowerPreferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Singleton
internal class PreferencesImpl
@Inject
internal constructor(
    private val context: Context,
) : PowerPreferences {

  private val preferences by lazy {
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
  }

  private suspend fun removeOldPreferences() =
      withContext(context = Dispatchers.IO) {
        if (preferences.contains(OldKeys.EXIT_WHILE_CHARGING)) {
          preferences.edit { remove(OldKeys.EXIT_WHILE_CHARGING) }
        }
      }

  override suspend fun setPowerSavingEnabled(enable: Boolean) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        removeOldPreferences()

        preferences.edit { putBoolean(KEY_POWER_SAVING_ENABLED, enable) }
      }

  override suspend fun observePowerSavingEnabled(): Flow<Boolean> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        removeOldPreferences()

        return@withContext preferences.booleanFlow(
            KEY_POWER_SAVING_ENABLED,
            DEFAULT_POWER_SAVING_ENABLED,
        )
      }

  override suspend fun setIgnoreInPowerSavingMode(ignore: Boolean) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        removeOldPreferences()

        preferences.edit { putBoolean(KEY_IGNORE_IN_POWER_SAVING_MODE, ignore) }
      }

  override suspend fun observeIgnoreInPowerSavingMode(): Flow<Boolean> =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        removeOldPreferences()

        return@withContext preferences.booleanFlow(
            KEY_IGNORE_IN_POWER_SAVING_MODE,
            DEFAULT_IGNORE_IN_POWER_SAVING_MODE,
        )
      }

  private object OldKeys {

    const val EXIT_WHILE_CHARGING = "key_exit_while_charging_v1"
  }

  companion object {

    private const val KEY_POWER_SAVING_ENABLED = "key_power_saving_enabled_v1"
    private const val DEFAULT_POWER_SAVING_ENABLED = true

    private const val KEY_IGNORE_IN_POWER_SAVING_MODE = "key_ignore_in_power_saving_mode_v1"
    private const val DEFAULT_IGNORE_IN_POWER_SAVING_MODE = true
  }
}
