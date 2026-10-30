package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.models

import at.stefanirndorfer.batmate.domain.club.ClubOverview

sealed interface ClubUiState {
    data object Loading : ClubUiState

    /** Only reachable from [ClubSource.Mine]: the user has chosen neither My Player nor My Club. */
    data object NoClub : ClubUiState

    data object Error : ClubUiState

    data class Content(val overview: ClubOverview, val isFavorite: Boolean = false) : ClubUiState
}
