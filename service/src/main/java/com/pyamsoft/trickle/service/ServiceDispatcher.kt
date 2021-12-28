package com.pyamsoft.trickle.service

import com.pyamsoft.pydroid.notify.NotifyData
import com.pyamsoft.pydroid.notify.NotifyDispatcher

interface ServiceDispatcher : NotifyDispatcher<ServiceDispatcher.Data> {

  data class Data internal constructor(val isPowerSavingEnabled: Boolean?) : NotifyData
}
