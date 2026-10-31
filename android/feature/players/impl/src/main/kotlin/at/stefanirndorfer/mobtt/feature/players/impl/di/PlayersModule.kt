package at.stefanirndorfer.mobtt.feature.players.impl.di

import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.PlayerDetailViewModel
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.PlayersViewModel
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.players.models.PlayersMode
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playersModule = module {
    viewModel { params -> PlayersViewModel(params.get<PlayersMode>(), get(), get(), get(), get(), get(), get()) }
    viewModel { params -> PlayerDetailViewModel(params.get<String>(), get(), get(), get(), get(), get()) }
}
