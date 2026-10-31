package at.stefanirndorfer.batmate.feature.club.impl.presentation.leaguedetail.models

import at.stefanirndorfer.batmate.domain.club.LeagueDetail

sealed interface LeagueDetailUiState {
    data object Loading : LeagueDetailUiState

    data object Error : LeagueDetailUiState

    data class Content(val detail: LeagueDetail) : LeagueDetailUiState
}
