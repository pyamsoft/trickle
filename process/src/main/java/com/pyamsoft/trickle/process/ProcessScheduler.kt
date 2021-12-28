package com.pyamsoft.trickle.process

interface ProcessScheduler {

  suspend fun cancel()

  suspend fun schedulePowerSaving(enable: Boolean)
}
