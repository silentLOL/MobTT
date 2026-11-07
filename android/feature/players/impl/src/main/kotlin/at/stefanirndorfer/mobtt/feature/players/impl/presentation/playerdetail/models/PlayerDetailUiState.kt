package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models

import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.user.PlayerClub
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams

sealed interface PlayerDetailUiState {
    data object Loading : PlayerDetailUiState

    /** The Player cannot be resolved: unknown, never picked, or XTTV unreachable. */
    data object Unavailable : PlayerDetailUiState

    /**
     * The Player is known, so the screen draws at once. [club] and [teams] load on their own; a loaded [club] is null
     * when XTTV no longer resolves the Club. The stat row rides on [teams]. [ratingHistory] is null while hidden.
     */
    data class Content(
        val player: Player,
        val isFavorite: Boolean = false,
        val club: SectionUi<PlayerClub?> = SectionUi.Loading,
        val teams: SectionUi<PlayerTeams> = SectionUi.Loading,
        val ratingHistory: RatingHistorySectionUi? = null,
    ) : PlayerDetailUiState
}

/**
 * The collapsed Rating history section, shown only for a Player with a Ratings Central entry. [history] loads the first
 * time the section is opened.
 */
data class RatingHistorySectionUi(
    val expanded: Boolean = false,
    val history: SectionUi<RatingHistory> = SectionUi.Loading,
)
