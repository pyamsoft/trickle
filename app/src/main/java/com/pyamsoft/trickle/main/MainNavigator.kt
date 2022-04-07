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

package com.pyamsoft.trickle.main

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.pyamsoft.pydroid.ui.navigator.FragmentNavigator
import com.pyamsoft.pydroid.ui.navigator.Navigator
import com.pyamsoft.trickle.home.HomeFragment
import javax.inject.Inject
import javax.inject.Named

internal class MainNavigator
@Inject
internal constructor(
    activity: MainActivity,
    @IdRes @Named("main_container") fragmentContainerId: Int,
) : FragmentNavigator<MainPage>(activity, fragmentContainerId) {

  override fun performFragmentTransaction(
      container: Int,
      data: FragmentTag,
      newScreen: Navigator.Screen<MainPage>,
      previousScreen: MainPage?
  ) {
    commitNow {
      decideAnimationForPage(newScreen.screen, previousScreen)
      replace(container, data.fragment(newScreen.arguments), data.tag)
    }
  }

  override fun provideFragmentTagMap(): Map<MainPage, FragmentTag> {
    return mapOf(
        MainPage.Home to createFragmentTag("HomeFragment") { HomeFragment.newInstance() },
    )
  }

  companion object {

    private fun FragmentTransaction.decideAnimationForPage(newPage: MainPage, oldPage: MainPage?) {
      // TODO
    }

    @JvmStatic
    @CheckResult
    private inline fun createFragmentTag(
        tag: String,
        crossinline fragment: (arguments: Bundle?) -> Fragment,
    ): FragmentTag {
      return object : FragmentTag {
        override val fragment: (arguments: Bundle?) -> Fragment = { fragment(it) }
        override val tag: String = tag
      }
    }
  }
}
