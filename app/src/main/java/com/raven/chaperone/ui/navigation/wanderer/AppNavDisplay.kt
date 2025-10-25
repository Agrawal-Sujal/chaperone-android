package com.raven.chaperone.ui.navigation.wanderer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.raven.chaperone.ui.navigation.wanderer.Screen
import com.raven.chaperone.ui.screens.wanderer.explore.search.SearchPageScreen
import com.raven.chaperone.ui.screens.wanderer.explore.searchResult.SearchResultScreen

@Composable
fun AppNavDisplay() {
    val backstack = remember { mutableStateListOf<Screen>() }
    backstack.add(Screen.ExplorePage)
    val current = backstack.lastOrNull() ?: Screen.ExplorePage
    NavDisplay(
        backStack = backstack,
        onBack = { backstack.removeLastOrNull() },
        modifier = Modifier,
        entryProvider = entryProvider {
            entry<Screen.ExplorePage> {
                SearchPageScreen(
                    goToResultScreen = { searchData ->
                        backstack.add(Screen.SearchResult(searchData))
                    }
                )
            }
            entry<Screen.SearchResult> {
                SearchResultScreen(it.searchData)
            }
        }
    )
}