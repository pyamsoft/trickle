package com.pyamsoft.trickle.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.CheckResult
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.util.preferenceBooleanFlow
import com.pyamsoft.pydroid.util.preferenceIntFlow
import com.pyamsoft.trickle.BuildConfig
import com.pyamsoft.trickle.battery.PowerPreferences
import com.pyamsoft.trickle.core.InAppRatingPreferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber

@Singleton
internal class PreferencesImpl
@Inject
internal constructor(
    private val enforcer: ThreadEnforcer,
    context: Context,
) : PowerPreferences, InAppRatingPreferences {

  private val preferences by lazy {
    enforcer.assertOffMainThread()
    PreferenceManager.getDefaultSharedPreferences(context.applicationContext).also {
      cleanOldPreferences(it)
    }
  }

  private val scope by lazy {
    CoroutineScope(
        context = SupervisorJob() + Dispatchers.Default + CoroutineName(this::class.java.name),
    )
  }

  private fun cleanOldPreferences(preferences: SharedPreferences) {
    // Commit to enforce this happens before we continue further
    preferences.edit(commit = true) {
      remove(OldKeys.EXIT_WHILE_CHARGING)
      remove(OldKeys.KEY_IGNORE_IN_POWER_SAVING_MODE)
    }
  }

  @CheckResult
  private fun isInAppRatingAlreadyShown(): Boolean {
    enforcer.assertOffMainThread()
    val version = preferences.getInt(IN_APP_RATING_SHOWN_VERSION, 0)
    return version.isInAppRatingAlreadyShown()
  }

  @CheckResult
  private fun Int.isInAppRatingAlreadyShown(): Boolean {
    enforcer.assertOffMainThread()
    return this > 0 && this == BuildConfig.VERSION_CODE
  }

  override fun listenShowInAppRating(): Flow<Boolean> =
      combineTransform(
              preferenceIntFlow(IN_APP_APP_OPENED, 0) { preferences },
              preferenceIntFlow(IN_APP_RATING_SHOWN_VERSION, 0) { preferences },
          ) { appOpened, lastVersionShown ->
            enforcer.assertOffMainThread()

            Timber.d(
                "In app rating check: ${mapOf(
          "lastVersion" to lastVersionShown,
          "isAlreadyShown" to lastVersionShown.isInAppRatingAlreadyShown(),
          "appOpened" to appOpened,
        )}")

            if (lastVersionShown.isInAppRatingAlreadyShown()) {
              Timber.w("Already shown in-app rating for version: $lastVersionShown")
              emit(false)
            } else {
              val show = appOpened >= 5
              emit(show)

              if (show) {
                // Commit this edit so that it fires immediately before we process again
                preferences.edit(commit = true) {
                  // Reset the previous flags
                  putInt(IN_APP_APP_OPENED, 0)

                  // And mark the latest version
                  putInt(IN_APP_RATING_SHOWN_VERSION, BuildConfig.VERSION_CODE)
                }
              }
            }
          }
          // Need this or we run on the main thread
          .flowOn(context = Dispatchers.Default)

  override fun markAppOpened() {
    scope.launch {
      enforcer.assertOffMainThread()

      if (!isInAppRatingAlreadyShown()) {
        // Not atomic because shared prefs are lame
        preferences.updateInt(IN_APP_APP_OPENED, 0) { it + 1 }
      }
    }
  }

  override fun togglePowerSavingEnabled() {
    scope.launch {
      enforcer.assertOffMainThread()

      preferences.updateBoolean(KEY_POWER_SAVING_ENABLED, DEFAULT_POWER_SAVING_ENABLED) { !it }
    }
  }

  override fun setPowerSavingEnabled(enable: Boolean) {
    scope.launch {
      enforcer.assertOffMainThread()

      preferences.edit { putBoolean(KEY_POWER_SAVING_ENABLED, enable) }
    }
  }

  override fun observePowerSavingEnabled(): Flow<Boolean> =
      preferenceBooleanFlow(KEY_POWER_SAVING_ENABLED, DEFAULT_POWER_SAVING_ENABLED) { preferences }
          .flowOn(context = Dispatchers.Default)

  private fun SharedPreferences.updateInt(key: String, defaultValue: Int, update: (Int) -> Int) {
    val self = this

    // Kinda atomic-ey
    while (true) {
      val prevValue = self.getInt(key, defaultValue)
      val nextValue = update(prevValue)
      synchronized(self) { self.edit { putInt(key, nextValue) } }
      return
    }
  }

  private fun SharedPreferences.updateBoolean(
      key: String,
      defaultValue: Boolean,
      update: (Boolean) -> Boolean
  ) {
    val self = this

    // Kinda atomic-ey
    while (true) {
      val prevValue = self.getBoolean(key, defaultValue)
      val nextValue = update(prevValue)
      synchronized(self) { self.edit { putBoolean(key, nextValue) } }
      return
    }
  }

  private object OldKeys {

    const val EXIT_WHILE_CHARGING = "key_exit_while_charging_v1"
    const val KEY_IGNORE_IN_POWER_SAVING_MODE = "key_ignore_in_power_saving_mode_v1"
  }

  companion object {

    private const val KEY_POWER_SAVING_ENABLED = "key_power_saving_enabled_v1"
    private const val DEFAULT_POWER_SAVING_ENABLED = true

    private const val IN_APP_APP_OPENED = "key_in_app_app_opened_1"

    private const val IN_APP_RATING_SHOWN_VERSION = "key_in_app_rating_shown_version"
  }
}
