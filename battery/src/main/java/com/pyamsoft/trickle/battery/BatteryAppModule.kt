package com.pyamsoft.trickle.battery

import androidx.annotation.CheckResult
import com.pyamsoft.trickle.battery.charging.BatteryCharge
import com.pyamsoft.trickle.battery.charging.BatteryChargeImpl
import com.pyamsoft.trickle.battery.optimize.BatteryOptimizer
import com.pyamsoft.trickle.battery.optimize.BatteryOptimizerImpl
import com.pyamsoft.trickle.battery.permission.PermissionGuard
import com.pyamsoft.trickle.battery.permission.PermissionGuardImpl
import com.pyamsoft.trickle.battery.saving.PowerSaverImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BatteryAppModule {

  @Binds
  @CheckResult
  internal abstract fun bindBatteryOptimizer(impl: BatteryOptimizerImpl): BatteryOptimizer

  @Binds @CheckResult internal abstract fun bindPowerSaving(impl: PowerSaverImpl): PowerSaver

  @Binds
  @CheckResult
  internal abstract fun bindBatteryCharge(impl: BatteryChargeImpl): BatteryCharge

  @Binds
  @CheckResult
  internal abstract fun bindPermissionChecker(impl: PermissionGuardImpl): PermissionGuard
}
