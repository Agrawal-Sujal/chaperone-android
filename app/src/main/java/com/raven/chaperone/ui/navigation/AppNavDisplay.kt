package com.raven.chaperone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.raven.chaperone.ui.screens.onboarding.OnboardingPageScreen
import com.raven.chaperone.ui.screens.onboarding.SplashScreen
import com.raven.chaperone.ui.screens.onboarding.signin.SignInScreen

@Composable
fun AppNavDisplay() {
    val backstack = remember { mutableStateListOf<Screen>(Screen.Splash) }
    val current = backstack.lastOrNull() ?: Screen.Splash

    NavDisplay(
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        modifier = Modifier,
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashScreen({ backstack.add(Screen.OnboardingPage(0)) })
            }

            entry<Screen.OnboardingPage> {
                OnboardingPageScreen(it.page) {
                    if (it.page == 2) {
                        backstack.add(Screen.SignIn)
                    } else
                        backstack.add(Screen.OnboardingPage(it.page + 1))

                }
            }

            entry<Screen.SignIn> {
                SignInScreen(onSignInSuccess = {}, onNavigateToSignUp = {})
            }
        }
    )

}