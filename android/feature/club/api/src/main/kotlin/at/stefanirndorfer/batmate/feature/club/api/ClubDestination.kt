package at.stefanirndorfer.batmate.feature.club.api

import at.stefanirndorfer.batmate.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

@Serializable
data object ClubDestination : GlobalAppDestination

fun GlobalNavigator.navigateToClub() = navigateTopLevel(ClubDestination)
