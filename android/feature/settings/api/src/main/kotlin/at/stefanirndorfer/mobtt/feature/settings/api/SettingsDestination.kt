package at.stefanirndorfer.mobtt.feature.settings.api

import at.stefanirndorfer.mobtt.feature.navigation.GlobalAppDestination
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import kotlinx.serialization.Serializable

/** Settings, pushed on top of the current tab so Back returns to where it was opened (ADR-0012). */
@Serializable
data object SettingsDestination : GlobalAppDestination

fun GlobalNavigator.navigateToSettings() = navigate(SettingsDestination)
