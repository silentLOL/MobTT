package at.stefanirndorfer.batmate.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.club.api.navigateToClubDetail
import at.stefanirndorfer.batmate.feature.club.api.navigateToTeamDetail
import at.stefanirndorfer.batmate.feature.home.api.HomeDestination
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.HomeRoute
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import at.stefanirndorfer.batmate.feature.players.api.navigateToPlayerDetail

fun EntryProviderScope<NavKey>.homeGraph(navigator: GlobalNavigator) {
    entry<HomeDestination> {
        HomeRoute(
            onOpenSettings = navigator::openSettings,
            onOpenClub = navigator::navigateToClubDetail,
            onOpenPlayer = navigator::navigateToPlayerDetail,
            onOpenTeam = navigator::navigateToTeamDetail,
        )
    }
}
