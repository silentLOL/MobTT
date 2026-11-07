package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models

import at.stefanirndorfer.mobtt.domain.user.PlayerRankingRow

/** The League's Player ranking, loaded on its own so the table and the Fixtures never wait for it. */
sealed interface PlayerRankingUi {
    data object Loading : PlayerRankingUi

    data object Unavailable : PlayerRankingUi

    data class Loaded(val rows: List<PlayerRankingRow>) : PlayerRankingUi
}
