package com.pyamsoft.trickle.process

import com.pyamsoft.trickle.process.work.PowerSaverInjector
import dagger.Subcomponent

@Subcomponent
interface ProcessComponent {

  fun inject(injector: PowerSaverInjector)
}
