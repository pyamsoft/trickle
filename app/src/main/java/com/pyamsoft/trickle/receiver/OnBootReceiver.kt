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

package com.pyamsoft.trickle.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pyamsoft.trickle.ObjectGraph
import com.pyamsoft.trickle.battery.PowerPreferences
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.service.ServiceLauncher
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OnBootReceiver internal constructor() : BroadcastReceiver() {

  @Inject @JvmField internal var launcher: ServiceLauncher? = null
  @Inject @JvmField internal var preferences: PowerPreferences? = null

  // Once we are flagged done, don't run this again
  // Some manufacturers send BOOT_COMPLETED more than once because of course they do.
  private val done = MutableStateFlow(false)

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
      if (done.compareAndSet(expect = false, update = true)) {
        Timber.d { "Boot completed, check for service start!" }

        ObjectGraph.ApplicationScope.retrieve(context).inject(this)
        val p = preferences
        if (p == null) {
          Timber.w { "Could not start BootReceiver: preferences NULL after inject" }
          return
        }
        val l = launcher
        if (l == null) {
          Timber.w { "Could not start BootReceiver: launcher NULL after inject" }
          return
        }

        MainScope().launch(context = Dispatchers.Default) {
          if (p.observePowerSavingEnabled().first()) {
            withContext(context = Dispatchers.Main) {
              Timber.d { "Start service on boot" }
              l.start()
            }
          }

          preferences = null
          launcher = null
        }
      }
    }
  }
}
