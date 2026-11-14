package at.stefanirndorfer.mobtt.feature.club.impl.presentation.teamdetail.models

import at.stefanirndorfer.mobtt.domain.club.TeamDetail

sealed interface TeamDetailUiState {
    data object Loading : TeamDetailUiState

    data object Error : TeamDetailUiState

    data class Content(val detail: TeamDetail, val isFavorite: Boolean = false) : TeamDetailUiState
}
