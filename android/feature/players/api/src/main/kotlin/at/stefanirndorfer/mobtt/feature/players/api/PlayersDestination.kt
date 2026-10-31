package at.stefanirndorfer.mobtt.feature.players.api

import at.stefanirndorfer.mobtt.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

@Serializable
data object PlayersDestination : GlobalAppDestination

/** A specific Player pushed on top of the current tab, e.g. from a Favorite. */
@Serializable
data class PlayerDetailDestination(val playerId: String) : GlobalAppDestination

/** The Players search where tapping a Player makes them My Player; pushed from Home's choose prompt. */
@Serializable
data object ChoosePlayerDestination : GlobalAppDestination

fun GlobalNavigator.navigateToPlayers() = navigateTopLevel(PlayersDestination)

fun GlobalNavigator.navigateToPlayerDetail(playerId: String) = navigate(PlayerDetailDestination(playerId))

fun GlobalNavigator.navigateToChoosePlayer() = navigate(ChoosePlayerDestination)
