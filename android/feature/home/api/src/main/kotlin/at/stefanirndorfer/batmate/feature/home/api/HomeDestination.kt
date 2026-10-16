package at.stefanirndorfer.batmate.feature.home.api

import at.stefanirndorfer.batmate.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.batmate.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination : GlobalAppDestination

fun GlobalNavigator.navigateToHome() = navigateTopLevel(HomeDestination)
