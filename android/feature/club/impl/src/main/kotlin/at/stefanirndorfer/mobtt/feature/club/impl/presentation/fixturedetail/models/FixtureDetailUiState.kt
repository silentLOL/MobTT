package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.models

import at.stefanirndorfer.mobtt.domain.club.FixtureDetail

sealed interface FixtureDetailUiState {
    data object Loading : FixtureDetailUiState

    data object Error : FixtureDetailUiState

    /** [canShare] is false until sharing Fixtures is switched on for this build. */
    data class Content(val detail: FixtureDetail, val canShare: Boolean = false) : FixtureDetailUiState
}
