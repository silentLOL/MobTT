package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models

import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.user.PlayerClub
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams

sealed interface PlayerDetailUiState {
    data object Loading : PlayerDetailUiState

    /** The Player cannot be resolved: unknown, never picked, or XTTV unreachable. */
    data object Unavailable : PlayerDetailUiState

    /**
     * The Player is known, so the screen draws at once. [club] and [teams] load on their own; a loaded [club] is null
     * when XTTV no longer resolves the Club. The stat row rides on [teams].
     */
    data class Content(
        val player: Player,
        val isFavorite: Boolean = false,
        val club: SectionUi<PlayerClub?> = SectionUi.Loading,
        val teams: SectionUi<PlayerTeams> = SectionUi.Loading,
    ) : PlayerDetailUiState
}
