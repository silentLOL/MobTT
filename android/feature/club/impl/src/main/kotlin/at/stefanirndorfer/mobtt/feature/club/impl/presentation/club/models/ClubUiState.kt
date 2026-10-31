package at.stefanirndorfer.mobtt.feature.club.impl.presentation.club.models

import at.stefanirndorfer.mobtt.core.ui.directory.models.AssociationClubUi
import at.stefanirndorfer.mobtt.domain.club.ClubOverview

sealed interface ClubUiState {
    data object Loading : ClubUiState

    /** Only reachable from [ClubSource.Mine]: the user has chosen neither My Player nor My Club, so [picker] finds a Club. */
    data class NoClub(val picker: AssociationClubUi = AssociationClubUi()) : ClubUiState

    data object Error : ClubUiState

    data class Content(val overview: ClubOverview, val isFavorite: Boolean = false) : ClubUiState
}
