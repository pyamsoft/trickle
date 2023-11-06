package com.pyamsoft.trickle.battery.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PermissionGuardImpl
@Inject
internal constructor(
    private val context: Context,
) : PermissionGuard {

  @CheckResult
  private fun hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context.applicationContext, permission) ==
        PackageManager.PERMISSION_GRANTED
  }

  override fun canWriteSystemSettings(): Boolean {
    return SYSTEM_POWER_PERMISSIONS.all { hasPermission(it) }
  }

  companion object {
    private val SYSTEM_POWER_PERMISSIONS =
        listOf(
            // To control Settings.Global
            android.Manifest.permission.WRITE_SECURE_SETTINGS,
        )
  }
}
