package com.pyamsoft.trickle.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.notify.NotifyChannelInfo
import com.pyamsoft.pydroid.notify.NotifyData
import com.pyamsoft.pydroid.notify.NotifyDispatcher
import com.pyamsoft.pydroid.notify.NotifyId
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import timber.log.Timber

@Singleton
internal class ServiceDispatcher
@Inject
internal constructor(
    private val context: Context,
    @StringRes @Named("app_name") private val appNameRes: Int,
    @DrawableRes @Named("app_icon") private val smallNotificationIcon: Int,
    private val activityClass: Class<out Activity>,
) : NotifyDispatcher<ServiceDispatcher.Data> {

  private val channelCreator by lazy {
    context.applicationContext.getSystemService<NotificationManager>().requireNotNull()
  }

  private fun guaranteeNotificationChannelExists(channelInfo: NotifyChannelInfo) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationGroup = NotificationChannelGroup(channelInfo.id, channelInfo.title)
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
  private fun createNotificationBuilder(
      channelInfo: NotifyChannelInfo
  ): NotificationCompat.Builder {
    return NotificationCompat.Builder(context.applicationContext, channelInfo.id)
        .setSmallIcon(smallNotificationIcon)
        .setShowWhen(false)
        .setAutoCancel(false)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setSilent(true)
        .setContentIntent(getActivityPendingIntent())
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
  }

  @CheckResult
  private fun hydrateNotification(channelInfo: NotifyChannelInfo): Notification {
    guaranteeNotificationChannelExists(channelInfo)

    return createNotificationBuilder(channelInfo)
        .setContentTitle(context.getString(appNameRes))
        .setContentText("Monitoring")
        .build()
  }

  override fun build(
      id: NotifyId,
      channelInfo: NotifyChannelInfo,
      notification: Data
  ): Notification {
    return hydrateNotification(channelInfo)
  }

  override fun canShow(notification: NotifyData): Boolean {
    return notification is Data
  }

  companion object {

    private const val REQUEST_CODE_ACTIVITY = 1337420
  }

  object Data : NotifyData
}
