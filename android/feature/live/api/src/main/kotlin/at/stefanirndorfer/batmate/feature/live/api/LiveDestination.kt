package at.stefanirndorfer.batmate.feature.live.api

import at.stefanirndorfer.batmate.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

@Serializable
data object LiveDestination : GlobalAppDestination

fun GlobalNavigator.navigateToLive() = navigateTopLevel(LiveDestination)
