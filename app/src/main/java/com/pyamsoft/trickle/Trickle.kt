package com.pyamsoft.trickle

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.ui.ModuleProvider
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.installPYDroid
import com.pyamsoft.pydroid.util.isDebugMode
import com.pyamsoft.trickle.receiver.OnBootReceiver

internal class Trickle : Application() {

  @CheckResult
  private fun installPYDroid(): ModuleProvider {
    val url = "https://github.com/pyamsoft/trickle"

    return installPYDroid(
        PYDroid.Parameters(
            viewSourceUrl = url,
            bugReportUrl = "$url/issues",
            privacyPolicyUrl = PRIVACY_POLICY_URL,
            termsConditionsUrl = TERMS_CONDITIONS_URL,
            version = BuildConfig.VERSION_CODE,
            logger = createLogger(),
        ),
    )
  }

  private fun installComponent(moduleProvider: ModuleProvider) {
    val mods = moduleProvider.get()
    val component =
        DaggerTrickleComponent.factory()
            .create(
                debug = isDebugMode(),
                application = this,
                theming = mods.theming(),
                enforcer = mods.enforcer(),
            )
    ObjectGraph.ApplicationScope.install(this, component)
  }

  /** Ensure the BootReceiver is set to state enabled */
  private fun ensureBootReceiverEnabled() {
    val componentName = ComponentName(this, OnBootReceiver::class.java)
    packageManager.setComponentEnabledSetting(
        componentName,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP,
    )
  }

  override fun onCreate() {
    super.onCreate()
    installLogger()
    val modules = installPYDroid()
    installComponent(modules)
    addLibraries()
    ensureBootReceiverEnabled()
  }

  companion object {

    @JvmStatic
    private fun addLibraries() {
      OssLibraries.apply {
        usingNotify = true
        usingAutopsy = true
        usingArch = true
        usingUi = true
      }

      OssLibraries.add(
          "Dagger",
          "https://github.com/google/dagger",
          "A fast dependency injector for Android and Java.",
      )

      OssLibraries.add(
          "LeakCanary",
          "https://github.com/square/leakcanary",
          "A memory leak detection library for Android.",
      )

      OssLibraries.add(
          "Timber",
          "https://github.com/JakeWharton/timber",
          "A logger with a small, extensible API which provides utility on top of Android's normal Log class.",
      )

      OssLibraries.add(
          "KSP",
          "https://github.com/google/ksp",
          "Kotlin Symbol Processing API",
      )

      OssLibraries.add(
          "AndroidX Appcompat",
          "https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-main/appcompat/",
          "AndroidX compatibility library for older versions of Android",
      )

      OssLibraries.add(
          "AndroidX Activity Compose",
          "https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/activity/activity-compose",
          "Jetpack Compose bridge for AndroidX Activity",
      )
    }

    const val PRIVACY_POLICY_URL = "https://pyamsoft.blogspot.com/p/trickle-privacy-policy.html"
    const val TERMS_CONDITIONS_URL =
        "https://pyamsoft.blogspot.com/p/trickle-terms-and-conditions.html"
  }
}
