package com.raven.chaperone.ui.navigation


sealed class Screen {
    object Splash : Screen()
    data class OnboardingPage(val page: Int) : Screen()
    object SignIn : Screen()
    data class SignUp(val stage: Int) : Screen()
    data class WandererQuestions(val page: Int) : Screen()

    data class Feedback(val walkerId:Int):Screen()

}