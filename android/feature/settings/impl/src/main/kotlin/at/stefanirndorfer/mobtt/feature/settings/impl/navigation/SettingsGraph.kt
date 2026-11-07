package at.stefanirndorfer.mobtt.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import at.stefanirndorfer.mobtt.feature.settings.api.SettingsDestination
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.SettingsRoute

fun EntryProviderScope<NavKey>.settingsGraph(@Suppress("UNUSED_PARAMETER") navigator: GlobalNavigator) {
    entry<SettingsDestination> {
        SettingsRoute()
    }
}
