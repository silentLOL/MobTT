package at.stefanirndorfer.batmate.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.home.api.HomeDestination
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.HomeScreen
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator

@Suppress("UNUSED_PARAMETER")
fun EntryProviderScope<NavKey>.homeGraph(navigator: GlobalNavigator) {
    entry<HomeDestination> {
        HomeScreen()
    }
}
