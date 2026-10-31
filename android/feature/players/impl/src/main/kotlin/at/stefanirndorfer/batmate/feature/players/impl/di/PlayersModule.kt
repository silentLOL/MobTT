package at.stefanirndorfer.batmate.feature.players.impl.di

import at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.PlayerDetailViewModel
import at.stefanirndorfer.batmate.feature.players.impl.presentation.players.PlayersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playersModule = module {
    viewModel { PlayersViewModel(get(), get(), get(), get()) }
    viewModel { params -> PlayerDetailViewModel(params.get<String>(), get(), get(), get(), get()) }
}
