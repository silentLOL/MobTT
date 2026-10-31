package at.stefanirndorfer.batmate.feature.players.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.club.api.navigateToClubDetail
import at.stefanirndorfer.batmate.feature.club.api.navigateToTeamDetail
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import at.stefanirndorfer.batmate.feature.players.api.PlayerDetailDestination
import at.stefanirndorfer.batmate.feature.players.api.PlayersDestination
import at.stefanirndorfer.batmate.feature.players.api.navigateToPlayerDetail
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.PlayerDetailRoute
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.PlayersRoute

fun EntryProviderScope<NavKey>.playersGraph(navigator: GlobalNavigator) {
    entry<PlayersDestination> {
        PlayersRoute(onOpenPlayer = navigator::navigateToPlayerDetail)
    }
    entry<PlayerDetailDestination> { destination ->
        PlayerDetailRoute(
            playerId = destination.playerId,
            onOpenClub = navigator::navigateToClubDetail,
            onOpenTeam = navigator::navigateToTeamDetail,
        )
    }
}
