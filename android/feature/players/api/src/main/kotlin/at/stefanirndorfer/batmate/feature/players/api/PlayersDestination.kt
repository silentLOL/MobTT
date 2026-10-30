package at.stefanirndorfer.batmate.feature.players.api

import at.stefanirndorfer.batmate.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

@Serializable
data object PlayersDestination : GlobalAppDestination

/** A specific Player pushed on top of the current tab, e.g. from a Favorite. */
@Serializable
data class PlayerDetailDestination(val playerId: String) : GlobalAppDestination

fun GlobalNavigator.navigateToPlayers() = navigateTopLevel(PlayersDestination)

fun GlobalNavigator.navigateToPlayerDetail(playerId: String) = navigate(PlayerDetailDestination(playerId))
