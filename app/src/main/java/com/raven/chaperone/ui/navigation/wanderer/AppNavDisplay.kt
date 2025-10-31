package com.raven.chaperone.ui.navigation.wanderer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.raven.chaperone.ui.screens.commonComponents.maps.MapSearchScreen
import com.raven.chaperone.ui.screens.payment.PaymentDetailScreen
import com.raven.chaperone.ui.screens.wanderer.explore.search.SearchPageScreen
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.SearchResultScreen
import com.raven.chaperone.ui.screens.wanderer.explore.walkerProfile.WalkerInfoScreen
import com.raven.chaperone.ui.screens.wanderer.feedback.FeedbackScreen
import com.raven.chaperone.ui.screens.wanderer.home.HomeScreen
import com.raven.chaperone.ui.screens.wanderer.locationSharing.LocationSharingScreen
import com.raven.chaperone.ui.screens.wanderer.walks.home.WalksHomeScreen
import com.raven.chaperone.ui.theme.lightPurple
import com.raven.chaperone.ui.theme.textPurple


sealed class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector) {
    data object Home : BottomNavItem(Screen.HomeScreen, "Home", Icons.Default.Home)

    data object Walks : BottomNavItem(Screen.WalksHomeScreen, "Walks", Icons.Default.DirectionsRun)
    data object Explore : BottomNavItem(Screen.ExplorePage(), "Explore", Icons.Default.Search)
}

@Composable
fun AppNavDisplay() {
    val backstack = remember { mutableStateListOf<Screen>(Screen.ExplorePage()) }

    val current = backstack.lastOrNull() ?: Screen.ExplorePage()

    val showBottomNav =
        current is Screen.ExplorePage || current is Screen.WalksHomeScreen || current is Screen.HomeScreen

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Walks,
    )
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(containerColor = Color.White) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = current::class == item.screen::class,
                            onClick = {
                                backstack.clear()
                                backstack.add(item.screen)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = textPurple,
                                selectedTextColor = textPurple,
                                indicatorColor = lightPurple,
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavDisplay(
            backStack = backstack,
            onBack = { backstack.removeLastOrNull() },
            modifier = Modifier.padding(paddingValues),
            entryProvider = entryProvider {
                entry<Screen.ExplorePage> {
                    SearchPageScreen(
                        goToResultScreen = { searchData ->
                            backstack.add(Screen.SearchResult(searchData))
                        },
                        goToMapScreen = { selectedLocation, locationName ->
                            backstack.add(Screen.MapScreen(selectedLocation, locationName))
                        },
                        selectedLocation = it.selectedLocation,
                        locationName = it.locationName
                    )
                }
                entry<Screen.MapScreen> { it ->
                    MapSearchScreen(
                        onLocationSelected = { location, name ->
                            backstack.removeLastOrNull()
                            val current = backstack.lastOrNull()
                            if (current == Screen.ExplorePage()) {
                                backstack.removeLastOrNull()
                                backstack.add(Screen.ExplorePage(location, name))
                            }

                        },
                        selectedLocation = it.selectedLocation,
                        locationName = it.locationName
                    )
                }
                entry<Screen.SearchResult> {
                    SearchResultScreen(
                        it.searchData, onViewProfileClick = { walkerProfileView ->
                            backstack.add(Screen.WalkerInfo(walkerProfileView, null))
                        },
                        onBackClick = {
                            backstack.removeLastOrNull()
                        }
                    )
                }

                entry<Screen.WalkerInfo> {
                    WalkerInfoScreen(onBackClick = {
                        backstack.removeLastOrNull()
                    }, walkerProfileView = it.walkerProfileView, walkerId = it.walkerId)
                }

                entry<Screen.WalksHomeScreen> {
                    WalksHomeScreen(onNavigateToProfile = {
                        backstack.add(Screen.WalkerInfo(null, it))
                    }, goToPaymentDetailScreen = {
                        backstack.add(Screen.PaymentDetailScreen(it))
                    }, trackLocation = {
                        backstack.add(Screen.LocationSharingScreen(it))
                    })
                }
                entry<Screen.LocationSharingScreen> {
                    LocationSharingScreen(it.roomId, goToWalkerFeedBackScreen = { walkerId ->
                        backstack.add(Screen.Feedback(walkerId))
                    })
                }
                entry<Screen.HomeScreen> {
                    HomeScreen()
                }
                entry<Screen.PaymentDetailScreen> {
                    PaymentDetailScreen(paymentId = it.paymentId)
                }

                entry<Screen.Feedback> {
                    FeedbackScreen(it.walkerId, goBack = {
                        backstack.clear()
                        backstack.add(Screen.ExplorePage())
                    })
                }
            }
        )

    }
}