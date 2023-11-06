package com.pyamsoft.trickle.battery.permission

import androidx.annotation.CheckResult

interface PermissionGuard {

  @CheckResult fun canWriteSystemSettings(): Boolean
}
