package com.pyamsoft.trickle.preference

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.util.PreferenceListener
import com.pyamsoft.pydroid.util.onChange
import com.pyamsoft.trickle.process.PowerPreferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
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

  override suspend fun isPowerSavingEnabled(): Boolean =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        return@withContext preferences.getBoolean(
            KEY_POWER_SAVING_ENABLED, DEFAULT_POWER_SAVING_ENABLED)
      }

  override suspend fun setPowerSavingEnabled(enable: Boolean) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        preferences.edit { putBoolean(KEY_POWER_SAVING_ENABLED, enable) }
      }

  override fun observerPowerSavingEnabled(onChange: (Boolean) -> Unit): PreferenceListener {
    return preferences.onChange(KEY_POWER_SAVING_ENABLED) { onChange(isPowerSavingEnabled()) }
  }

  override suspend fun isIgnoreInPowerSavingMode(): Boolean =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        return@withContext preferences.getBoolean(
            KEY_IGNORE_IN_POWER_SAVING_MODE, DEFAULT_IGNORE_IN_POWER_SAVING_MODE)
      }

  override suspend fun setIgnoreInPowerSavingMode(ignore: Boolean) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        preferences.edit { putBoolean(KEY_IGNORE_IN_POWER_SAVING_MODE, ignore) }
      }

  override fun observerIgnoreInPowerSavingMode(onChange: (Boolean) -> Unit): PreferenceListener {
    return preferences.onChange(KEY_IGNORE_IN_POWER_SAVING_MODE) {
      onChange(isIgnoreInPowerSavingMode())
    }
  }

  companion object {

    private const val KEY_POWER_SAVING_ENABLED = "key_power_saving_enabled_v1"
    private const val DEFAULT_POWER_SAVING_ENABLED = true

    private const val KEY_IGNORE_IN_POWER_SAVING_MODE = "key_ignore_in_power_saving_mode_v1"
    private const val DEFAULT_IGNORE_IN_POWER_SAVING_MODE = true
  }
}
