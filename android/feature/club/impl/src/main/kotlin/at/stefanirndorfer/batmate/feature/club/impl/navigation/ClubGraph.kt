package at.stefanirndorfer.batmate.feature.club.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.club.api.ClubDestination
import at.stefanirndorfer.batmate.feature.club.api.ClubDetailDestination
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.ClubRoute
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator

fun EntryProviderScope<NavKey>.clubGraph(navigator: GlobalNavigator) {
    entry<ClubDestination> {
        ClubRoute(source = ClubSource.Mine, onOpenSettings = navigator::openSettings)
    }
    entry<ClubDetailDestination> { destination ->
        ClubRoute(source = ClubSource.Specific(destination.clubId), onOpenSettings = navigator::openSettings)
    }
}
