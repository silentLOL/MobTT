package at.stefanirndorfer.mobtt.feature.settings.impl.di

import at.stefanirndorfer.mobtt.feature.settings.api.AppearanceRepository
import at.stefanirndorfer.mobtt.feature.settings.impl.data.DataStoreAppearanceRepository
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.consent.UsageConsentViewModel
import at.stefanirndorfer.mobtt.feature.settings.impl.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    singleOf(::DataStoreAppearanceRepository) bind AppearanceRepository::class
    viewModelOf(::SettingsViewModel)
    viewModelOf(::UsageConsentViewModel)
}
