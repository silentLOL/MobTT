package at.stefanirndorfer.batmate.feature.live.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.live.api.LiveDestination
import at.stefanirndorfer.batmate.feature.live.impl.presentation.live.LiveScreen
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator

@Suppress("UNUSED_PARAMETER")
fun EntryProviderScope<NavKey>.liveGraph(navigator: GlobalNavigator) {
    entry<LiveDestination> {
        LiveScreen()
    }
}
