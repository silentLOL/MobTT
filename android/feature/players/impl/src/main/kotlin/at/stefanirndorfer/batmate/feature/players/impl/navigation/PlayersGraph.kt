package at.stefanirndorfer.batmate.feature.players.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import at.stefanirndorfer.batmate.feature.players.api.PlayerDetailDestination
import at.stefanirndorfer.batmate.feature.players.api.PlayersDestination
import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.PlayerDetailRoute
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.PlayersScreen

@Suppress("UNUSED_PARAMETER")
fun EntryProviderScope<NavKey>.playersGraph(navigator: GlobalNavigator) {
    entry<PlayersDestination> {
        PlayersScreen()
    }
    entry<PlayerDetailDestination> { destination ->
        PlayerDetailRoute(playerId = destination.playerId)
    }
}
