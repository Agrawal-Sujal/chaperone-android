package com.raven.chaperone.ui.navigation

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Home : Screen()
}