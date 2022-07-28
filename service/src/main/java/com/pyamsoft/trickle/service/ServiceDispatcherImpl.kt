package com.pyamsoft.trickle.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.notify.NotifyChannelInfo
import com.pyamsoft.pydroid.notify.NotifyData
import com.pyamsoft.pydroid.notify.NotifyId
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import timber.log.Timber

@Singleton
internal class ServiceDispatcherImpl
@Inject
internal constructor(
    private val context: Context,
    @StringRes @Named("app_name") private val appNameRes: Int,
    private val activityClass: Class<out Activity>,
    private val serviceClass: Class<out Service>,
) : ServiceDispatcher {

  private val channelCreator by lazy {
    context.applicationContext.getSystemService<NotificationManager>().requireNotNull()
  }

  private fun guaranteeNotificationChannelExists(channelInfo: NotifyChannelInfo) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationGroup =
          NotificationChannelGroup("${channelInfo.id} Group", "${channelInfo.title} Group")
      val notificationChannel =
          NotificationChannel(
                  channelInfo.id,
                  channelInfo.title,
                  NotificationManager.IMPORTANCE_MIN,
              )
              .apply {
                group = notificationGroup.id
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                description = channelInfo.description
                enableLights(false)
                enableVibration(true)
              }

      Timber.d("Create notification channel and group: ${channelInfo.id} ${channelInfo.title}")
      channelCreator.apply {
        // Delete the group if it already exists with a bad group ID
        // Group ID and channel ID cannot match
        if (notificationChannelGroups.firstOrNull { it.id == channelInfo.id } != null) {
          deleteNotificationChannelGroup(channelInfo.id)
        }
        createNotificationChannelGroup(notificationGroup)
        createNotificationChannel(notificationChannel)
      }
    }
  }

  @CheckResult
  private fun getActivityPendingIntent(): PendingIntent {
    val appContext = context.applicationContext
    val activityIntent =
        Intent(appContext, activityClass).apply {
          flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    return PendingIntent.getActivity(
        appContext,
        REQUEST_CODE_ACTIVITY,
        activityIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
  }

  @CheckResult
  private fun getServicePendingIntent(
      requestCode: Int,
      options: PendingIntentOptions
  ): PendingIntent {
    val appContext = context.applicationContext
    val serviceIntent =
        Intent(appContext, serviceClass).apply {
          putExtra(ServiceNotification.KEY_TOGGLE_POWER_SAVING, options.togglePowerSaving)
        }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      PendingIntent.getForegroundService(
          appContext,
          requestCode,
          serviceIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
    } else {
      PendingIntent.getService(
          appContext,
          requestCode,
          serviceIntent,
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )
    }
  }

  @CheckResult
  private fun generateNotificationAction(
      name: String,
      intent: PendingIntent
  ): NotificationCompat.Action {
    return NotificationCompat.Action.Builder(0, name, intent)
        .setAllowGeneratedReplies(false)
        .setShowsUserInterface(false)
        .setContextual(false)
        .build()
  }

  @CheckResult
  private fun createNotificationBuilder(
      channelInfo: NotifyChannelInfo
  ): NotificationCompat.Builder {
    return NotificationCompat.Builder(context.applicationContext, channelInfo.id)
        .setSmallIcon(R.drawable.ic_notification_24)
        .setShowWhen(false)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setContentIntent(getActivityPendingIntent())
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_DEFERRED)
  }

  @CheckResult
  private fun hydrateNotification(
      channelInfo: NotifyChannelInfo,
      isPowerSavingEnabled: Boolean?,
  ): Notification {
    guaranteeNotificationChannelExists(channelInfo)

    val builder =
        createNotificationBuilder(channelInfo).setContentTitle(context.getString(appNameRes))

    if (isPowerSavingEnabled == null) {
      return builder.setContentText("Monitoring Power-Saving mode").build()
    }

    val currentState = if (isPowerSavingEnabled) "ENABLED" else "DISABLED"
    val nextState = if (isPowerSavingEnabled) "Disable" else "Enable"
    return builder
        .setContentText("Auto Power-Saving is: $currentState")
        .addAction(
            generateNotificationAction(
                nextState,
                getServicePendingIntent(
                    REQUEST_CODE_POWER_SAVING,
                    // Flip the setting to ensure it toggles
                    PendingIntentOptions(togglePowerSaving = !isPowerSavingEnabled),
                ),
            ),
        )
        .build()
  }

  override fun build(
      id: NotifyId,
      channelInfo: NotifyChannelInfo,
      notification: ServiceDispatcher.Data
  ): Notification {
    return hydrateNotification(
        channelInfo,
        notification.isPowerSavingEnabled,
    )
  }

  override fun canShow(notification: NotifyData): Boolean {
    return notification is ServiceDispatcher.Data
  }

  private data class PendingIntentOptions(val togglePowerSaving: Boolean)

  companion object {

    private const val REQUEST_CODE_ACTIVITY = 1337420
    private const val REQUEST_CODE_POWER_SAVING = REQUEST_CODE_ACTIVITY + 1
  }
}
