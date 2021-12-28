package com.pyamsoft.trickle.service

import android.app.Service
import com.pyamsoft.pydroid.notify.Notifier
import com.pyamsoft.pydroid.notify.NotifyChannelInfo
import com.pyamsoft.pydroid.notify.toNotifyId
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class ServiceLauncher
@Inject
internal constructor(
    @param:ServiceInternalApi private val notifier: Notifier,
) {

  fun createNotification(service: Service) {
    return notifier.startForeground(
            service = service,
            id = NOTIFICATION_ID,
            channelInfo = CHANNEL_INFO,
            notification = ServiceDispatcher.Data,
        )
        .let { Timber.d("Started foreground notification: $it") }
  }

  fun stopNotification(service: Service) {
    notifier.stopForeground(service, NOTIFICATION_ID)
  }

  companion object {

    private val NOTIFICATION_ID = 42069.toNotifyId()

    private val CHANNEL_INFO =
        NotifyChannelInfo(
            id = "channel_monitor_service",
            title = "Monitor Service",
            description = "Monitor device screen state",
        )
  }
}
