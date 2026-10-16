package at.stefanirndorfer.batmate.feature.players.api

import at.stefanirndorfer.batmate.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

@Serializable
data object PlayersDestination : GlobalAppDestination

fun GlobalNavigator.navigateToPlayers() = navigateTopLevel(PlayersDestination)
