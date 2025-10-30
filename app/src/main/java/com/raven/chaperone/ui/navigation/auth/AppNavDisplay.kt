package com.raven.chaperone.ui.navigation.auth

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.raven.chaperone.data.local.appPref.AppPref
import com.raven.chaperone.ui.navigation.walker.WalkerAppNavDisplay
import com.raven.chaperone.ui.navigation.wanderer.AppNavDisplay
import com.raven.chaperone.ui.screens.auth.AuthScreen
import com.raven.chaperone.ui.screens.auth.ExtraInfoScreen
import com.raven.chaperone.ui.screens.auth.IdVerificationScreen
import com.raven.chaperone.ui.screens.onboarding.OnboardingPageScreen
import com.raven.chaperone.ui.screens.onboarding.SplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavDisplay(context: Context) {

    val appPref = AppPref(context)
    val backstack = remember { mutableStateListOf<Screen>() }
    runBlocking {
        val token = appPref.token.first()
        val isIdVerified = appPref.isIdVerified.first()
        val isWalker = appPref.isWalker.first()
        if (token == null) {
            backstack.add(Screen.Splash)
        } else if (isIdVerified == null) {
            backstack.add(Screen.IdVerification)
        } else if (isWalker == null) {
            backstack.add(Screen.ExtraInfoScreen)
        } else {
            if (isWalker) {
                backstack.add(Screen.WalkerHome)
            } else backstack.add(Screen.WandererHome)
        }
    }
    NavDisplay(
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashScreen {
                    backstack.clear()
                    backstack.add(Screen.OnboardingPage(0))
                }
            }

            entry<Screen.OnboardingPage> {
                OnboardingPageScreen(it.page) {
                    if (it.page == 2) {
                        backstack.clear()
                        backstack.add(Screen.SignInUp)
                    } else
                        backstack.add(Screen.OnboardingPage(it.page + 1))
                }
            }
            entry<Screen.SignInUp> {
                AuthScreen(
                    goToIdVerificationScreen = {
                        backstack.add(Screen.IdVerification)
                    },
                    goToProfileScreen = {
                        backstack.add(Screen.ExtraInfoScreen)
                    },
                    onSuccess = { isWalker ->
                        backstack.clear()
                        if (isWalker)
                            backstack.add(Screen.WalkerHome)
                        else backstack.add(Screen.WandererHome)
                    })
            }
            entry<Screen.IdVerification> {
                IdVerificationScreen(goToExtraInfoScreen = {
                    backstack.add(Screen.ExtraInfoScreen)
                })
            }

            entry<Screen.ExtraInfoScreen> {
                ExtraInfoScreen(goToWalkerHomePage = {
                    backstack.clear()
                    backstack.add(Screen.WalkerHome)
                }, goToWandererHomePage = {
                    backstack.clear()
                    backstack.add(Screen.WandererHome)
                })
            }

            entry<Screen.WalkerHome> {
                WalkerAppNavDisplay()
            }
            entry<Screen.WandererHome> {
                AppNavDisplay()
            }
        }
    )
}