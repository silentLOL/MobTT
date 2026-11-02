package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models

import at.stefanirndorfer.mobtt.domain.club.FixtureDetail

sealed interface FixtureDetailUiState {
    data object Loading : FixtureDetailUiState

    data object Error : FixtureDetailUiState

    data class Content(val detail: FixtureDetail) : FixtureDetailUiState
}
