package at.stefanirndorfer.mobtt.feature.home.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.mobtt.feature.club.api.navigateToChooseClub
import at.stefanirndorfer.mobtt.feature.club.api.navigateToClubDetail
import at.stefanirndorfer.mobtt.feature.club.api.navigateToTeamDetail
import at.stefanirndorfer.mobtt.feature.home.api.HomeDestination
import at.stefanirndorfer.mobtt.feature.home.impl.presentation.home.HomeRoute
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import at.stefanirndorfer.mobtt.feature.players.api.navigateToChoosePlayer
import at.stefanirndorfer.mobtt.feature.players.api.navigateToPlayerDetail

fun EntryProviderScope<NavKey>.homeGraph(navigator: GlobalNavigator) {
    entry<HomeDestination> {
        HomeRoute(
            onChoosePlayer = navigator::navigateToChoosePlayer,
            onChooseClub = navigator::navigateToChooseClub,
            onOpenClub = navigator::navigateToClubDetail,
            onOpenPlayer = navigator::navigateToPlayerDetail,
            onOpenTeam = navigator::navigateToTeamDetail,
        )
    }
}
