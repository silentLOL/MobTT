package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatList
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatListRow
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSectionHeader
import at.stefanirndorfer.mobtt.domain.user.PlayerRankingRow
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.models.PlayerRankingUi
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.preview.playerRankingLongNamesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.preview.playerRankingPreview

/** The League's Player ranking below the Fixtures. Each row opens its Player; My Player's row is marked. */
@Composable
fun PlayerRankingSectionView(
    ranking: PlayerRankingUi,
    isMyPlayer: (playerId: String) -> Boolean,
    onPlayerClick: (playerId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        BatSectionHeader(title = stringResource(R.string.league_detail_ranking_title))
        when (ranking) {
            PlayerRankingUi.Loading -> {
                val loadingLabel = stringResource(R.string.league_detail_ranking_loading_cd)
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .padding(BatDesign.spacing.md)
                        .semantics { contentDescription = loadingLabel },
                    color = BatDesign.colors.brand,
                )
            }
            PlayerRankingUi.Unavailable -> SectionMessage(
                text = stringResource(R.string.league_detail_ranking_unavailable_body),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
            is PlayerRankingUi.Loaded -> if (ranking.rows.isEmpty()) {
                SectionMessage(stringResource(R.string.league_detail_ranking_empty_body))
            } else {
                BatList {
                    ranking.rows.forEachIndexed { index, row ->
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

@Composable
private fun SectionMessage(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, color = BatDesign.colors.textSecondary, style = BatDesign.typography.body)
}

@BatScreenPreview
@Composable
private fun PlayerRankingSectionViewPreview() {
    BatPreviewSurface {
        Column {
            PlayerRankingSectionView(PlayerRankingUi.Loaded(playerRankingPreview), isMyPlayer = { it == "gruber" }, onPlayerClick = {})
            PlayerRankingSectionView(PlayerRankingUi.Loaded(playerRankingLongNamesPreview), isMyPlayer = { false }, onPlayerClick = {})
            PlayerRankingSectionView(PlayerRankingUi.Loading, isMyPlayer = { false }, onPlayerClick = {})
            PlayerRankingSectionView(PlayerRankingUi.Unavailable, isMyPlayer = { false }, onPlayerClick = {})
            PlayerRankingSectionView(PlayerRankingUi.Loaded(emptyList()), isMyPlayer = { false }, onPlayerClick = {})
        }
    }
}
