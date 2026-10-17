package at.stefanirndorfer.batmate.feature.club.impl.di

import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.ClubViewModel
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val clubModule = module {
    viewModel { params -> ClubViewModel(params.get<ClubSource>(), get(), get()) }
}
