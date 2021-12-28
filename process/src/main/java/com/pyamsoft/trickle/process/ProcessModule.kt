package com.pyamsoft.trickle.process

import androidx.annotation.CheckResult
import com.pyamsoft.trickle.process.permission.PermissionChecker
import com.pyamsoft.trickle.process.permission.PermissionCheckerImpl
import com.pyamsoft.trickle.process.work.PowerSaver
import com.pyamsoft.trickle.process.work.PowerSaverImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ProcessModule {

  @Binds @CheckResult internal abstract fun bindPowerSaving(impl: PowerSaverImpl): PowerSaver

  @Binds
  @CheckResult
  internal abstract fun bindPermissionChecker(impl: PermissionCheckerImpl): PermissionChecker
}
