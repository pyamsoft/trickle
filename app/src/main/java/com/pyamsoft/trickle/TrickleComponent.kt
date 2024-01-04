/*
 * Copyright 2021 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.trickle

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.bus.internal.DefaultEventBus
import com.pyamsoft.pydroid.core.ThreadEnforcer
import com.pyamsoft.pydroid.notify.NotifyGuard
import com.pyamsoft.pydroid.notify.NotifyPermission
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.pydroid.util.PermissionRequester
import com.pyamsoft.trickle.battery.BatteryAppModule
import com.pyamsoft.trickle.battery.PowerPreferences
import com.pyamsoft.trickle.core.InAppRatingPreferences
import com.pyamsoft.trickle.main.MainActivity
import com.pyamsoft.trickle.main.MainComponent
import com.pyamsoft.trickle.preference.PreferencesImpl
import com.pyamsoft.trickle.receiver.OnBootReceiver
import com.pyamsoft.trickle.service.A14WorkAround
import com.pyamsoft.trickle.service.DefaultA14WorkAround
import com.pyamsoft.trickle.service.MonitorService
import com.pyamsoft.trickle.service.ServiceAppModule
import com.pyamsoft.trickle.service.ServiceComponent
import com.pyamsoft.trickle.service.notification.PermissionRequests
import com.pyamsoft.trickle.service.notification.PermissionResponses
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules =
        [
            TrickleComponent.Provider::class,
            BatteryAppModule::class,
            ServiceAppModule::class,
        ],
)
internal interface TrickleComponent {

  fun inject(receiver: OnBootReceiver)

  @CheckResult fun plusMainComponent(): MainComponent.Factory

  @CheckResult fun plusServiceComponent(): ServiceComponent.Factory

  @Component.Factory
  interface Factory {

    @CheckResult
    fun create(
        @Named("debug") @BindsInstance debug: Boolean,
        @BindsInstance application: Application,
        @BindsInstance theming: Theming,
        @BindsInstance enforcer: ThreadEnforcer,
    ): TrickleComponent
  }

  @Module
  abstract class Provider {

    @Binds
    @CheckResult
    internal abstract fun bindPowerPreferences(impl: PreferencesImpl): PowerPreferences

    @Binds
    @CheckResult
    internal abstract fun bindInAppRatingPreferences(impl: PreferencesImpl): InAppRatingPreferences

    @Binds
    @CheckResult
    internal abstract fun bindA14WorkAround(impl: DefaultA14WorkAround): A14WorkAround

    @Module
    companion object {

      @Provides
      @JvmStatic
      @Singleton
      internal fun provideNotificationPermissionRequester(): PermissionRequester {
        return NotifyPermission.createDefault()
      }

      @Provides
      @JvmStatic
      @Singleton
      internal fun provideNotifyGuard(context: Context): NotifyGuard {
        return NotifyGuard.createDefault(context)
      }

      @Provides
      @JvmStatic
      internal fun provideContext(application: Application): Context {
        return application
      }

      @Provides
      @JvmStatic
      @Named("app_name")
      internal fun provideAppNameRes(): Int {
        return R.string.app_name
      }

      @Provides
      @JvmStatic
      internal fun provideActivityClass(): Class<out Activity> {
        return MainActivity::class.java
      }

      @Provides
      @JvmStatic
      internal fun provideServiceClass(): Class<out Service> {
        return MonitorService::class.java
      }

      @Provides
      @JvmStatic
      @Singleton
      internal fun providePermissionRequestBus(): EventBus<PermissionRequests> {
        return DefaultEventBus()
      }

      @Provides
      @JvmStatic
      @Singleton
      internal fun providePermissionResponseBus(): EventBus<PermissionResponses> {
        return DefaultEventBus()
      }
    }
  }
}
