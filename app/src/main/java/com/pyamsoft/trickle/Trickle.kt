package com.pyamsoft.trickle

import android.app.Application
import androidx.annotation.CheckResult
import coil.Coil
import com.pyamsoft.pydroid.bootstrap.libraries.OssLibraries
import com.pyamsoft.pydroid.ui.ModuleProvider
import com.pyamsoft.pydroid.ui.PYDroid
import com.pyamsoft.pydroid.util.isDebugMode
import kotlinx.coroutines.MainScope

internal class Trickle : Application() {

  private val applicationScope by lazy(LazyThreadSafetyMode.NONE) { MainScope() }

  private val component by lazy {
    val url = "https://github.com/pyamsoft/trickle"
    val parameters =
        PYDroid.Parameters(
            viewSourceUrl = url,
            bugReportUrl = "$url/issues",
            privacyPolicyUrl = PRIVACY_POLICY_URL,
            termsConditionsUrl = TERMS_CONDITIONS_URL,
            version = BuildConfig.VERSION_CODE,
            imageLoader = { Coil.imageLoader(this) },
            logger = createLogger(),
            theme = { themeProvider, content ->
              TrickleTheme(
                  themeProvider = themeProvider,
                  content = content,
              )
            },
        )

    return@lazy createComponent(PYDroid.init(this, parameters))
  }

  @CheckResult
  private fun createComponent(provider: ModuleProvider): TrickleComponent {
    return DaggerTrickleComponent.factory()
        .create(
            this,
            isDebugMode(),
            provider.get().theming(),
        )
        .also { addLibraries() }
  }

  override fun onCreate() {
    super.onCreate()
    component.inject(this)
  }

  override fun getSystemService(name: String): Any? {
    // Use component here in a weird way to guarantee the lazy is initialized.
    return component.run { PYDroid.getSystemService(name) } ?: fallbackGetSystemService(name)
  }

  @CheckResult
  private fun fallbackGetSystemService(name: String): Any? {
    return if (name == TrickleComponent::class.java.name) component
    else super.getSystemService(name)
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
