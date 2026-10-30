package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.models

import at.stefanirndorfer.batmate.domain.user.PlayerDetail

sealed interface PlayerDetailUiState {
    data object Loading : PlayerDetailUiState

    /** The Player cannot be resolved: unknown, never picked, or XTTV unreachable. */
    data object Unavailable : PlayerDetailUiState

    data class Content(val detail: PlayerDetail, val isFavorite: Boolean = false) : PlayerDetailUiState
}
