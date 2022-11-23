package com.pyamsoft.trickle

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.annotation.CheckResult
import coil.ImageLoader
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.core.requireNotNull
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.util.isDebugMode
import com.pyamsoft.trickle.receiver.OnBootReceiver
import timber.log.Timber

internal class Trickle : Application() {

  // Must be lazy since Coil calls getSystemService() internally,
  // leading to SO exception
  private val lazyImageLoader = lazy(LazyThreadSafetyMode.NONE) { ImageLoader(this) }

  // The order that the PYDroid instance and TrickleComponent instance are created is very specific.
  //
  // Coil lazy loader must be first, then PYDroid, and then Component
  private var pydroid: PYDroid? = null
  private var component: TrickleComponent? = null

  private fun installPYDroid() {
    if (pydroid == null) {
      val url = "https://github.com/pyamsoft/trickle"

      installLogger()

      pydroid =
          PYDroid.init(
              this,
              PYDroid.Parameters(
                  // Must be lazy since Coil calls getSystemService() internally,
                  // leading to SO exception
                  lazyImageLoader = lazyImageLoader,
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
    } else {
      Timber.w("Cannot install PYDroid again")
    }
  }

  private fun installComponent() {
    if (component == null) {
      val p = pydroid.requireNotNull { "Must install PYDroid before installing TrickleComponent" }
      component =
          DaggerTrickleComponent.factory()
              .create(
                  application = this,
                  debug = isDebugMode(),
                  theming = p.modules().theming(),
              )
    } else {
      Timber.w("Cannot install TrickleComponent again")
    }
  }

  @CheckResult
  private fun componentGraph(): TrickleComponent {
    return component.requireNotNull { "TrickleComponent was not installed, something is wrong." }
  }

  @CheckResult
  private fun fallbackGetSystemService(name: String): Any? {
    return if (name == TrickleComponent::class.java.name) componentGraph()
    else super.getSystemService(name)
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
    installPYDroid()
    installComponent()

    addLibraries()
    ensureBootReceiverEnabled()
  }

  override fun getSystemService(name: String): Any? {
    return pydroid?.getSystemService(name) ?: fallbackGetSystemService(name)
  }

  companion object {

    @JvmStatic
    private fun addLibraries() {
      // We are using pydroid-notify
      OssLibraries.usingNotify = true

      // We are using pydroid-autopsy
      OssLibraries.usingAutopsy = true

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
