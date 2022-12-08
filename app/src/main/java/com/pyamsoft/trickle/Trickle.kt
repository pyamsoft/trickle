package com.pyamsoft.trickle

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.annotation.CheckResult
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.ModuleProvider
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.ui.installPYDroid
import com.pyamsoft.pydroid.util.isDebugMode
import com.pyamsoft.trickle.receiver.OnBootReceiver
import timber.log.Timber

internal class Trickle : Application() {

  private var component: TrickleComponent? = null

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
            theme = TrickleThemeProvider,
            debug =
                PYDroid.DebugParameters(
                    enabled = true,
                    upgradeAvailable = true,
                    ratingAvailable = false,
                ),
        ),
    )
  }

  private fun installComponent(moduleProvider: ModuleProvider) {
    if (component == null) {
      component =
          DaggerTrickleComponent.factory()
              .create(
                  application = this,
                  debug = isDebugMode(),
                  theming = moduleProvider.get().theming(),
              )
    } else {
      Timber.w("Cannot install TrickleComponent again")
    }
  }

  @CheckResult
  private fun componentGraph(): TrickleComponent {
    return component.requireNotNull { "TrickleComponent was not installed, something is wrong." }
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

  override fun getSystemService(name: String): Any? {
    return if (name == TrickleComponent::class.java.name) componentGraph()
    else super.getSystemService(name)
  }

  companion object {

    @JvmStatic
    private fun addLibraries() {
      // We are using pydroid-notify
      OssLibraries.usingNotify = true

      // We are using pydroid-autopsy
      OssLibraries.usingAutopsy = true

      // We are using pydroid-inject
      OssLibraries.usingInject = true

      OssLibraries.add(
          "Dagger",
          "https://github.com/google/dagger",
          "A fast dependency injector for Android and Java.",
      )
    }

    const val PRIVACY_POLICY_URL = "https://pyamsoft.blogspot.com/p/trickle-privacy-policy.html"
    const val TERMS_CONDITIONS_URL =
        "https://pyamsoft.blogspot.com/p/trickle-terms-and-conditions.html"
  }
}
