package com.pyamsoft.trickle.process.permission

import androidx.annotation.CheckResult

interface PermissionChecker {

  @CheckResult suspend fun hasSecureSettingsPermission(): Boolean
}
