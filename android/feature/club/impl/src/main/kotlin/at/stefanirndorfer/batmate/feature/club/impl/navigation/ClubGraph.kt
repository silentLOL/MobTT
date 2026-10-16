package at.stefanirndorfer.batmate.feature.club.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.club.api.ClubDestination
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.ClubScreen
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator

@Suppress("UNUSED_PARAMETER")
fun EntryProviderScope<NavKey>.clubGraph(navigator: GlobalNavigator) {
    entry<ClubDestination> {
        ClubScreen()
    }
}
