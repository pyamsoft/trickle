package com.pyamsoft.trickle.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import com.pyamsoft.trickle.process.permission.PermissionChecker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ServiceLauncherImpl
@Inject
internal constructor(
    private val context: Context,
    private val permissionChecker: PermissionChecker,
    private val serviceClass: Class<out Service>,
) : ServiceLauncher {

  private fun startService() {
    val service = Intent(context, serviceClass)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(service)
    } else {
      context.startService(service)
    }
  }

  override suspend fun launch() {
    if (permissionChecker.hasSecureSettingsPermission()) {
      startService()
    }
  }
}
