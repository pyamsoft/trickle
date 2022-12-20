package com.pyamsoft.trickle.service

import android.app.Service
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bus.EventConsumer
import com.pyamsoft.pydroid.core.requireNotNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Singleton
class ServiceHandler
@Inject
internal constructor(
    private val notificationRefreshListener: EventConsumer<NotificationRefreshEvent>,
    private val notification: ServiceNotification,
) {

  private val scope by lazy(LazyThreadSafetyMode.NONE) { MainScope() }

  private var toggleJob: Job? = null
  private var parentJob: Job? = null

  @CheckResult
  private fun Job?.cancelAndReLaunch(block: suspend CoroutineScope.() -> Unit): Job {
    this?.cancel()
    return scope.launch(context = Dispatchers.Main, block = block)
  }

  fun bind(service: Service) {
    // Watch everything else as the parent
    parentJob =
        parentJob.cancelAndReLaunch {
          // Watch for notification refresh
          launch(context = Dispatchers.Main) {
            notificationRefreshListener.requireNotNull().onEvent {
              Timber.d("Refresh notification")
              notification.updateNotification(service)
            }
          }

          // Launch the notification again to refresh it with correct pref state
          launch(context = Dispatchers.Main) { notification.updateNotification(service) }
        }
  }

  fun toggle(
      service: Service,
      enable: Boolean,
  ) {
    toggleJob =
        toggleJob.cancelAndReLaunch {
          Timber.d("Toggle Power-Saving: $enable")
          notification.updateNotification(service, enable)
        }
  }

  fun destroy() {
    toggleJob?.cancel()
    toggleJob = null

    parentJob?.cancel()
    parentJob = null
  }
}
