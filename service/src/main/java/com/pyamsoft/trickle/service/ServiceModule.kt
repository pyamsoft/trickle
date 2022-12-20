package com.pyamsoft.trickle.service

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.bus.EventConsumer
import com.pyamsoft.pydroid.notify.Notifier
import com.pyamsoft.pydroid.notify.NotifyDispatcher
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier @Retention(AnnotationRetention.BINARY) internal annotation class ServiceInternalApi

@Module
abstract class ServiceModule {

  @Binds
  @IntoSet
  @ServiceInternalApi
  internal abstract fun bindNotifyDispatcher(impl: ServiceDispatcher): NotifyDispatcher<*>

  @Binds internal abstract fun bindDispatcher(impl: ServiceDispatcherImpl): ServiceDispatcher

  @Binds internal abstract fun bindNotification(impl: ServiceNotificationImpl): ServiceNotification

  @Binds internal abstract fun bindLauncher(impl: ServiceLauncherImpl): ServiceLauncher

  @Binds
  @CheckResult
  internal abstract fun bindNotificationRefreshConsumer(
      impl: EventBus<NotificationRefreshEvent>
  ): EventConsumer<NotificationRefreshEvent>

  @Module
  companion object {

    @Provides
    @JvmStatic
    @Singleton
    internal fun provideNotificationRefreshEventBus(): EventBus<NotificationRefreshEvent> {
      return EventBus.create()
    }

    @Provides
    @Singleton
    @JvmStatic
    @CheckResult
    @ServiceInternalApi
    internal fun provideNotifier(
        // Need to use MutableSet instead of Set because of Java -> Kotlin fun.
        @ServiceInternalApi dispatchers: MutableSet<NotifyDispatcher<*>>,
        context: Context
    ): Notifier {
      return Notifier.createDefault(context, dispatchers)
    }
  }
}
