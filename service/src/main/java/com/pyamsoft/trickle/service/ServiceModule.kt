package com.pyamsoft.trickle.service

import android.content.Context
import androidx.annotation.CheckResult
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
  internal abstract fun bindDispatcher(impl: ServiceDispatcher): NotifyDispatcher<*>

  @Module
  companion object {

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
