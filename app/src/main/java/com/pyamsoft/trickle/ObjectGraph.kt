package com.pyamsoft.trickle

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.util.doOnDestroy
import com.pyamsoft.trickle.core.Timber
import com.pyamsoft.trickle.main.MainComponent

internal object ObjectGraph {

  internal object ApplicationScope {

    private val trackingMap = mutableMapOf<Application, TrickleComponent>()

    fun install(
        application: Application,
        component: TrickleComponent,
    ) {
      trackingMap[application] = component
      Timber.d { "Track ApplicationScoped install: $application $component" }
    }

    @CheckResult
    fun retrieve(context: Context): TrickleComponent {
      return retrieve(context.applicationContext as Application)
    }

    @CheckResult
    fun retrieve(activity: Activity): TrickleComponent {
      return retrieve(activity.application)
    }

    @CheckResult
    fun retrieve(service: Service): TrickleComponent {
      return retrieve(service.application)
    }

    @CheckResult
    fun retrieve(application: Application): TrickleComponent {
      return trackingMap[application].requireNotNull {
        "Could not find ApplicationScoped internals for Application: $application"
      }
    }

    @CheckResult
    inline fun ensure(application: Application, block: () -> TrickleComponent): TrickleComponent {
      var held = trackingMap[application]
      if (held == null) {
        held = block()
        trackingMap[application] = held
      }

      return held
    }
  }

  internal object ActivityScope {

    private val trackingMap = mutableMapOf<ComponentActivity, MainComponent>()

    fun install(
        activity: ComponentActivity,
        component: MainComponent,
    ) {
      trackingMap[activity] = component
      Timber.d { "Track ActivityScoped install: $activity $component" }

      activity.doOnDestroy {
        Timber.d { "Remove ActivityScoped graph onDestroy" }
        trackingMap.remove(activity)
      }
    }

    @CheckResult
    fun retrieve(activity: ComponentActivity): MainComponent {
      return trackingMap[activity].requireNotNull {
        "Could not find ActivityScoped internals for Activity: $activity"
      }
    }
  }
}
