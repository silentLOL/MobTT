package at.stefanirndorfer.mobtt.feature.settings.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import at.stefanirndorfer.mobtt.feature.navigation.GlobalNavigator
import at.stefanirndorfer.mobtt.feature.settings.api.LicensesDestination
import at.stefanirndorfer.mobtt.feature.settings.api.SettingsDestination
import at.stefanirndorfer.mobtt.feature.settings.api.navigateToLicenses
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.licenses.LicensesRoute
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.SettingsRoute

fun EntryProviderScope<NavKey>.settingsGraph(navigator: GlobalNavigator) {
    entry<SettingsDestination> {
        SettingsRoute(onOpenLicenses = navigator::navigateToLicenses)
    }
    entry<LicensesDestination> {
        LicensesRoute()
    }
}
