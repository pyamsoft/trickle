package com.pyamsoft.trickle.main

sealed class MainPage(val name: String) {
  object Home : MainPage("Home")
}
