package com.pyamsoft.trickle.service.foreground

interface ScreenReceiver {

  suspend fun register()
}
