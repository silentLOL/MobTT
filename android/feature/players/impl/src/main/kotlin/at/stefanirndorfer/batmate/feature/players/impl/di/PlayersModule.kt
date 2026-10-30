package at.stefanirndorfer.batmate.feature.players.impl.di

import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.PlayerDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playersModule = module {
    viewModel { params -> PlayerDetailViewModel(params.get<String>(), get(), get(), get(), get()) }
}
