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
import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.ui.theme.Theming
import com.pyamsoft.trickle.main.MainActivity
import com.pyamsoft.trickle.main.MainComponent
import com.pyamsoft.trickle.process.workmanager.ProcessModule
import com.pyamsoft.trickle.receiver.ScreenReceiver
import com.pyamsoft.trickle.service.ServiceComponent
import com.pyamsoft.trickle.service.ServiceModule
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
            TrickleComponent.TrickleProvider::class,
            ProcessModule::class,
            ServiceModule::class,
        ],
)
internal interface TrickleComponent {

  fun inject(receiver: ScreenReceiver)

  fun inject(application: Trickle)

  @CheckResult fun plusMainComponent(): MainComponent.Factory

  @CheckResult fun plusServiceComponent(): ServiceComponent.Factory

  @Component.Factory
  interface Factory {

    @CheckResult
    fun create(
        @BindsInstance application: Application,
        @Named("debug") @BindsInstance debug: Boolean,
        @BindsInstance theming: Theming,
    ): TrickleComponent
  }

  @Module
  abstract class TrickleProvider {

    @Module
    companion object {

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
      @Named("app_icon")
      internal fun provideAppIconRes(): Int {
        return R.mipmap.ic_launcher_round
      }

      @Provides
      @JvmStatic
      internal fun provideActivityClass(): Class<out Activity> {
        return MainActivity::class.java
      }
    }
  }
}
