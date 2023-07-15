/*
 * Copyright 2023 pyamsoft
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

package com.pyamsoft.trickle.service

import com.pyamsoft.trickle.service.foreground.ScreenReceiver
import com.pyamsoft.trickle.service.notification.NotificationLauncher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class ServiceRunner
@Inject
internal constructor(
    private val notificationLauncher: NotificationLauncher,
    private val screenReceiver: ScreenReceiver,
) {
  private val runningState = MutableStateFlow(false)

  private fun CoroutineScope.startPowerService() {
    val scope = this

    // Start notification first for Android O immediately
    scope.launch(context = Dispatchers.Default) { notificationLauncher.start() }

    // Register for Screen events
    scope.launch(context = Dispatchers.Default) { screenReceiver.register() }
  }

  /** Start the proxy */
  suspend fun start() =
      withContext(context = Dispatchers.Default) {
        if (runningState.compareAndSet(expect = false, update = true)) {
          try {
            Timber.d("Starting runner!")
            coroutineScope { startPowerService() }
          } finally {
            withContext(context = NonCancellable) {
              if (runningState.compareAndSet(expect = true, update = false)) {
                Timber.d("Stopping runner!")
              }
            }
          }
        }
      }
}
