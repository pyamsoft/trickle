package com.pyamsoft.trickle.battery.permission

import androidx.annotation.CheckResult

interface PermissionGuard {

  @get:CheckResult val requiredPermissions: List<String>

  @CheckResult fun canManageSystemPower(): Boolean
}
