package at.stefanirndorfer.batmate.feature.club.impl.di

import at.stefanirndorfer.batmate.feature.club.impl.presentation.chooseclub.ChooseClubViewModel
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.ClubViewModel
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models.ClubSource
import at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.LeagueDetailViewModel
import at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.TeamDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val clubModule = module {
    viewModel { params -> ClubViewModel(params.get<ClubSource>(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ChooseClubViewModel(get(), get(), get()) }
    viewModel { params -> LeagueDetailViewModel(params.get<String>(0), params.get<String?>(1), get()) }
    viewModel { params -> TeamDetailViewModel(params.get<String>(), get(), get(), get(), get(), get()) }
}
