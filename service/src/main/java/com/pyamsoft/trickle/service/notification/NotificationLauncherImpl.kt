package com.pyamsoft.trickle.service.notification

import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.notify.Notifier
import com.pyamsoft.pydroid.notify.NotifyChannelInfo
import com.pyamsoft.pydroid.notify.toNotifyId
import com.pyamsoft.trickle.service.ServiceInternalApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
internal class NotificationLauncherImpl
@Inject
internal constructor(
    @param:ServiceInternalApi private val notifier: Notifier,
    private val enforcer: ThreadEnforcer,
    private val permissionResponseBus: EventBus<PermissionResponses>,
) : NotificationLauncher {

  private val showing = MutableStateFlow(false)

  private fun stop() {
    enforcer.assertOnMainThread()

    Timber.d("Stop foreground notification")
    notifier.cancel(NOTIFICATION_ID)
  }

  private fun CoroutineScope.watchNotificationResponses() {
    val scope = this

    permissionResponseBus.also { f ->
      scope.launch(context = Dispatchers.Default) {
        f.collect { resp ->
          when (resp) {
            is PermissionResponses.Notification -> {
              notify()
            }
          }
        }
      }
    }
  }

  private suspend fun notify() =
      withContext(context = Dispatchers.Main) {
        notifier
            .show(
                id = NOTIFICATION_ID,
                channelInfo = CHANNEL_INFO,
                notification = NotificationData,
            )
            .also { Timber.d("Started foreground notification: $it") }
      }

  override suspend fun start() =
      withContext(context = Dispatchers.Default) {
        if (showing.compareAndSet(expect = false, update = true)) {
          try {
            // Hold this here until the coroutine is cancelled
            coroutineScope {
              watchNotificationResponses()
              notify()

              // And suspend until we are done
              awaitCancellation()
            }
          } finally {
            withContext(context = NonCancellable) {
              if (showing.compareAndSet(expect = true, update = false)) {
                withContext(context = Dispatchers.Main) { stop() }
              }
            }
          }
        }
      }

  companion object {

    private val NOTIFICATION_ID = 42069.toNotifyId()

    private val CHANNEL_INFO =
        NotifyChannelInfo(
            id = "channel_monitor_service_v1",
            title = "Monitor Service",
            description = "Monitor device screen state",
        )
  }
}
