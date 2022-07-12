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

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.pyamsoft.pydroid.arch.UiSavedStateReader
import com.pyamsoft.pydroid.arch.UiSavedStateWriter
import com.pyamsoft.pydroid.ui.navigator.FragmentNavigator
import com.pyamsoft.trickle.home.HomeFragment
import javax.inject.Inject
import javax.inject.Named

internal class MainNavigator
@Inject
internal constructor(
    activity: MainActivity,
    @IdRes @Named("main_container") fragmentContainerId: Int,
) : FragmentNavigator<MainPage>(activity, fragmentContainerId) {

  override fun onRestoreState(savedInstanceState: UiSavedStateReader) {}

  override fun onSaveState(outState: UiSavedStateWriter) {}

  override fun produceFragmentForScreen(screen: MainPage): Fragment =
      when (screen) {
        is MainPage.Home -> HomeFragment.newInstance()
      }

  override fun performFragmentTransaction(
      container: Int,
      newScreen: Fragment,
      previousScreen: Fragment?
  ) {
    commitNow { replace(container, newScreen, newScreen::class.java.name) }
  }
}
