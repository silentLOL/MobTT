package at.stefanirndorfer.mobtt.feature.home.api

import at.stefanirndorfer.mobtt.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination : GlobalAppDestination

fun GlobalNavigator.navigateToHome() = navigateTopLevel(HomeDestination)
