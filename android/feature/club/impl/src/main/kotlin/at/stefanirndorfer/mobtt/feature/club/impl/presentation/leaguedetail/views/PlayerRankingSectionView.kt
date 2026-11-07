package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatList
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatListRow
import at.stefanirndorfer.mobtt.domain.user.PlayerRankingRow
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.SectionUi
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.preview.playerRankingLongNamesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.preview.playerRankingPreview

/** The League's Player ranking below the Fixtures, collapsed until opened. Each row opens its Player; My Player's row is marked. */
@Composable
fun PlayerRankingSectionView(
    ranking: SectionUi<List<PlayerRankingRow>>,
    expanded: Boolean,
    onToggle: () -> Unit,
    isMyPlayer: (playerId: String) -> Boolean,
    onPlayerClick: (playerId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazySectionView(
        title = stringResource(R.string.league_detail_ranking_title),
        expanded = expanded,
        onToggle = onToggle,
        section = ranking,
        loadingLabel = stringResource(R.string.league_detail_ranking_loading_cd),
        unavailableText = stringResource(R.string.league_detail_ranking_unavailable_body),
        modifier = modifier,
    ) { rows ->
        if (rows.isEmpty()) {
            SectionMessage(stringResource(R.string.league_detail_ranking_empty_body))
        } else {
            BatList {
                rows.forEachIndexed { index, row ->
                    RankingRow(
                        row = row,
                        isMyPlayer = isMyPlayer(row.ranked.player.id),
                        striped = index % 2 == 1,
                        onClick = { onPlayerClick(row.ranked.player.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingRow(row: PlayerRankingRow, isMyPlayer: Boolean, striped: Boolean, onClick: () -> Unit) {
    val ranked = row.ranked
    val stats = stringResource(R.string.league_detail_ranking_stats_label, ranked.matchesWon, ranked.matchesLost, ranked.gamesPlayed)
    val badge = stringResource(R.string.league_detail_my_player_badge)
    val description = listOfNotNull(
        ranked.rank?.let { stringResource(R.string.league_detail_ranking_rank_cd, it) } ?: stringResource(R.string.league_detail_ranking_unranked_cd),
        ranked.player.name,
        badge.takeIf { isMyPlayer },
        row.teamName,
        stringResource(R.string.league_detail_ranking_stats_cd, ranked.matchesWon, ranked.matchesLost, ranked.gamesPlayed),
    ).joinToString(", ")
    BatListRow(
        title = ranked.player.name,
        onClick = onClick,
        leadingText = ranked.rank?.toString() ?: stringResource(R.string.league_detail_ranking_unranked_label),
        supportingText = listOfNotNull(row.teamName, stats).joinToString(" · "),
        badge = badge.takeIf { isMyPlayer },
        striped = striped,
        highlighted = isMyPlayer,
        contentDescription = description,
    )
}

@BatScreenPreview
@Composable
private fun PlayerRankingSectionViewPreview() {
    BatPreviewSurface {
        Column {
            PlayerRankingSectionView(
                SectionUi.Loaded(playerRankingPreview),
                expanded = true,
                onToggle = {},
                isMyPlayer = { it == "gruber" },
                onPlayerClick = {},
            )
            PlayerRankingSectionView(
                SectionUi.Loaded(playerRankingLongNamesPreview),
                expanded = true,
                onToggle = {},
                isMyPlayer = { false },
                onPlayerClick = {},
            )
            PlayerRankingSectionView(SectionUi.Loaded(emptyList()), expanded = true, onToggle = {}, isMyPlayer = { false }, onPlayerClick = {})
            PlayerRankingSectionView(SectionUi.Loading, expanded = false, onToggle = {}, isMyPlayer = { false }, onPlayerClick = {})
        }
    }
}
