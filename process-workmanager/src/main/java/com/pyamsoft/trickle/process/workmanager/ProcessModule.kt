package com.pyamsoft.trickle.process.workmanager

import androidx.annotation.CheckResult
import com.pyamsoft.trickle.process.ProcessScheduler
import dagger.Binds
import dagger.Module

@Module
abstract class ProcessModule {

  @Binds
  @CheckResult
  internal abstract fun bindProcessScheduler(impl: WorkManagerProcessScheduler): ProcessScheduler
}
