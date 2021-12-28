package com.pyamsoft.trickle.process.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.pyamsoft.pydroid.core.Enforcer
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
internal class PermissionCheckerImpl
@Inject
internal constructor(
    private val context: Context,
) : PermissionChecker {

  override suspend fun hasSecureSettingsPermission(): Boolean =
      withContext(context = Dispatchers.Default) {
        Enforcer.assertOffMainThread()
        val permission = android.Manifest.permission.WRITE_SECURE_SETTINGS
        val state = ContextCompat.checkSelfPermission(context.applicationContext, permission)
        return@withContext state == PackageManager.PERMISSION_GRANTED
      }
}
