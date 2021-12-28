package com.pyamsoft.trickle.process.work

import android.content.Context
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.inject.Injector
import com.pyamsoft.trickle.process.ProcessComponent
import javax.inject.Inject

class PowerSaverInjector(context: Context) {

  @Inject @JvmField internal var saver: PowerSaver? = null

  init {
    Injector.obtainFromApplication<ProcessComponent>(context).inject(this)
  }

  @CheckResult
  fun powerSaver(): PowerSaver {
    return saver.requireNotNull()
  }
}
