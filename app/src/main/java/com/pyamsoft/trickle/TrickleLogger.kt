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

import android.app.Application
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.PYDroidLogger
import com.pyamsoft.pydroid.ui.debug.InAppDebugLogger.Companion.createInAppDebugLogger
import com.pyamsoft.pydroid.util.isDebugMode
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

fun Application.installLogger(
    scope: CoroutineScope,
    inAppDebugStatus: InAppDebugStatus,
) {
  val self = this

  if (isDebugMode()) {
    Timber.plant(
        object : Timber.DebugTree() {
          override fun createStackElementTag(element: StackTraceElement): String {
            return element.run { "($fileName:$lineNumber)" }
          }
        },
    )
  }

    observeInAppDebugLogger(
        scope = scope,
        inAppDebugStatus = inAppDebugStatus,
    )
}

private fun Application.observeInAppDebugLogger(
    scope: CoroutineScope,
    inAppDebugStatus: InAppDebugStatus,
) {
    val self = this

    val tree =
        object : Timber.Tree() {

            private val logger by lazy { self.createInAppDebugLogger() }

            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                logger.log(priority, tag, message, t)
            }
        }

    val isPlanted = MutableStateFlow(false)
    inAppDebugStatus.listenForInAppDebuggingEnabled().also { f ->
        scope.launch(context = Dispatchers.Default) {
            f.collect { enabled ->
                if (enabled) {
                    if (isPlanted.compareAndSet(expect = false, update = true)) {
                        withContext(context = Dispatchers.Main) { Timber.plant(tree) }
                    }
                } else {
                    if (isPlanted.compareAndSet(expect = true, update = false)) {
                        withContext(context = Dispatchers.Main) { Timber.uproot(tree) }
                    }
                }
            }
        }
    }
}
}

@CheckResult
fun createLogger(): PYDroidLogger {
  return object : PYDroidLogger {

    override fun d(
        tag: String,
        message: String,
        vararg args: Any,
    ) {
      Timber.tag(tag).d(message, args)
    }

    override fun w(
        tag: String,
        message: String,
        vararg args: Any,
    ) {
      Timber.tag(tag).w(message, args)
    }

    override fun e(
        tag: String,
        throwable: Throwable,
        message: String,
        vararg args: Any,
    ) {
      Timber.tag(tag).e(throwable, message, args)
    }
  }
}
