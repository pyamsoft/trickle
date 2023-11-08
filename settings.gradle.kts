pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

rootProject.name = "Trickle"

include(":app")

include(":core")

include(":battery")

include(":home")

include(":main")

include(":service")

include(":settings")

include(":ui")
