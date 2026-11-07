package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models

import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.designsystem.ui.components.BAT_CHART_MAX_DOTS
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import at.stefanirndorfer.mobtt.domain.user.PlayerClub
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams
import kotlinx.datetime.LocalDate

sealed interface PlayerDetailUiState {
    data object Loading : PlayerDetailUiState

    /** The Player cannot be resolved: unknown, never picked, or XTTV unreachable. */
    data object Unavailable : PlayerDetailUiState

    /**
     * The Player is known, so the screen draws at once. [club] and [teams] load on their own; a loaded [club] is null
     * when XTTV no longer resolves the Club. The stat row rides on [teams]. [ratingHistory] is null while hidden; [ratingInfoShown] while its explanation is open.
     */
    data class Content(
        val player: Player,
        val isFavorite: Boolean = false,
        val club: SectionUi<PlayerClub?> = SectionUi.Loading,
        val teams: SectionUi<PlayerTeams> = SectionUi.Loading,
        val ratingHistory: RatingHistorySectionUi? = null,
        val ratingInfoShown: Boolean = false,
    ) : PlayerDetailUiState
}

/** How much of the Rating history the chart shows. */
enum class RatingHistorySpan { LastYear, All }

/**
 * The collapsed Rating history section, shown only for a Player with a Ratings Central entry. [history] loads the first
 * time the section is opened. [since] is the first day the chosen [span] covers, null for the whole history.
 */
data class RatingHistorySectionUi(
    val expanded: Boolean = false,
    val history: SectionUi<RatingHistory> = SectionUi.Loading,
    val span: RatingHistorySpan = RatingHistorySpan.LastYear,
    val since: LocalDate? = null,
) {
    /** The loaded Rating updates within [span], oldest first. */
    val visibleUpdates: List<RatingUpdate>
        get() = (history as? SectionUi.Loaded)?.value?.updates.orEmpty().filter { since == null || it.date >= since }

    /** The Player has Rating updates, just none in the chosen span. */
    val hasNoRecentUpdate: Boolean
        get() = visibleUpdates.isEmpty() && (history as? SectionUi.Loaded)?.value?.updates?.isNotEmpty() == true

    /** A dot per Rating update only while there are few enough to tell apart. */
    val showsDots: Boolean
        get() = visibleUpdates.size <= BAT_CHART_MAX_DOTS
}
