package com.raven.chaperone.ui.navigation

sealed class Screen {
    object Splash : Screen()
    data class OnboardingPage(val page: Int) : Screen()
    object SignIn : Screen()
    object Home : Screen()
}