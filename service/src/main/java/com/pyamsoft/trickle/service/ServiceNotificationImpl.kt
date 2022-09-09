package com.pyamsoft.trickle.service

import android.app.Service
import com.pyamsoft.pydroid.core.Enforcer
import com.pyamsoft.pydroid.notify.Notifier
import com.pyamsoft.pydroid.notify.NotifyChannelInfo
import com.pyamsoft.pydroid.notify.toNotifyId
import com.pyamsoft.trickle.process.PowerPreferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
internal class ServiceNotificationImpl
@Inject
internal constructor(
    @param:ServiceInternalApi private val notifier: Notifier,
    private val powerPreferences: PowerPreferences,
) : ServiceNotification {

  override fun createNotification(service: Service) {
    return notifier.startForeground(
            service = service,
            id = NOTIFICATION_ID,
            channelInfo = CHANNEL_INFO,
            notification = EMPTY_SERVICE_NOTIFICATION,
        )
        .let { Timber.d("Started foreground notification: $it") }
  }

  override suspend fun updateNotification(service: Service) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()

        val isPowerSavingEnabled = powerPreferences.observePowerSavingEnabled().first()
        return@withContext notifier.startForeground(
                service = service,
                id = NOTIFICATION_ID,
                channelInfo = CHANNEL_INFO,
                notification = ServiceDispatcher.Data(isPowerSavingEnabled = isPowerSavingEnabled),
            )
            .let { Timber.d("Started foreground notification: $it") }
      }

  override suspend fun togglePowerSavingEnabled(enable: Boolean) =
      withContext(context = Dispatchers.IO) {
        Enforcer.assertOffMainThread()
        powerPreferences.setPowerSavingEnabled(enable)
      }

  override fun stopNotification(service: Service) {
    notifier.stopForeground(service, NOTIFICATION_ID)
  }

  companion object {

    private val NOTIFICATION_ID = 42069.toNotifyId()

    private val EMPTY_SERVICE_NOTIFICATION = ServiceDispatcher.Data(isPowerSavingEnabled = null)

    private val CHANNEL_INFO =
        NotifyChannelInfo(
            id = "channel_monitor_service_v1",
            title = "Monitor Service",
            description = "Monitor device screen state",
        )
  }
}
