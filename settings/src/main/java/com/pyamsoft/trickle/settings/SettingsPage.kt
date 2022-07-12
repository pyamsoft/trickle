package com.pyamsoft.trickle.settings

sealed class SettingsPage(val name: String) {
  object Settings : SettingsPage("Settings")
}
