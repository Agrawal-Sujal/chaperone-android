package com.raven.chaperone.ui.navigation.auth



sealed class Screen {

    object Splash : Screen()

    data class OnboardingPage(val page: Int) : Screen()
    object SignInUp : Screen()

    object IdVerification: Screen()

    object ExtraInfoScreen: Screen()

    object WandererHome: Screen()

    object WalkerHome: Screen()
}