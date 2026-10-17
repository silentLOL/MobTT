package at.stefanirndorfer.batmate.feature.club.impl.presentation.teamdetail.models

import at.stefanirndorfer.batmate.domain.club.TeamDetail

sealed interface TeamDetailUiState {
    data object Loading : TeamDetailUiState

    data object Error : TeamDetailUiState

    data class Content(val detail: TeamDetail) : TeamDetailUiState
}
