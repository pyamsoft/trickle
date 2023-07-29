package com.pyamsoft.trickle.service

import android.app.Service
import android.content.Context
import android.content.Intent
import com.pyamsoft.trickle.battery.permission.PermissionGuard
import com.pyamsoft.trickle.core.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceLauncher
@Inject
internal constructor(
    serviceClass: Class<out Service>,
    private val context: Context,
    private val permission: PermissionGuard,
) {

  private val service by lazy(LazyThreadSafetyMode.NONE) { Intent(context, serviceClass) }

  private fun startService() {
    Timber.d { "Starting MonitoringService" }
    context.startService(service)
  }

  private fun stopService() {
    Timber.d { "Stopping MonitoringService" }
    context.stopService(service)
  }

  fun start() {
    if (permission.canManageSystemPower()) {
      startService()
    } else {
      Timber.w { "Cannot start MonitoringService - missing Permission" }
      stopService()
    }
  }

  fun stop() {
    stopService()
  }
}
