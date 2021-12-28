package com.pyamsoft.trickle.service

import android.app.Service

interface ServiceNotification {

  fun createNotification(service: Service)

  fun stopNotification(service: Service)

  suspend fun updateNotification()

  suspend fun togglePowerSavingEnabled(enable: Boolean)

  companion object {

    const val KEY_TOGGLE_POWER_SAVING = "key_toggle_power_saving"
  }
}
