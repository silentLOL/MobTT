package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatList
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatListItem
import at.stefanirndorfer.mobtt.domain.club.Match
import at.stefanirndorfer.mobtt.domain.club.MatchKind
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailLongNamesPreview
import at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview.fixtureDetailPlayedPreview

/** One Match of a Fixture: its number and kind, the players of both sides (the winner in bold) and the Sets won. */
@Composable
fun MatchRowView(match: Match, striped: Boolean, modifier: Modifier = Modifier) {
    val score = match.score
    val kind = stringResource(
        when (match.kind) {
            MatchKind.Doubles -> R.string.fixture_detail_match_doubles
            MatchKind.Singles -> R.string.fixture_detail_match_singles
        },
    )
    val home = match.homePlayers.joinToString(" / ")
    val away = match.awayPlayers.joinToString(" / ")
    val description = if (score == null) {
        stringResource(R.string.fixture_detail_match_row_no_result_cd, kind, match.number, home, away)
    } else {
        stringResource(R.string.fixture_detail_match_row_cd, kind, match.number, home, away, score.homeSetsWon, score.awaySetsWon)
    }
    BatListItem(modifier = modifier.semantics(mergeDescendants = true) {}, striped = striped, contentDescription = description) {
        Row(
            modifier = Modifier.fillMaxWidth().clearAndSetSemantics {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            Text(text = match.number.toString(), color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kind,
                    color = BatDesign.colors.textSecondary,
                    style = BatDesign.typography.caption,
                )
                Text(text = home, color = BatDesign.colors.textPrimary, style = playerStyle(isWinner = score != null && score.homeSetsWon > score.awaySetsWon))
                Text(text = away, color = BatDesign.colors.textPrimary, style = playerStyle(isWinner = score != null && score.awaySetsWon > score.homeSetsWon))
            }
            if (score != null) {
                Text(
                    text = stringResource(R.string.fixture_detail_score, score.homeSetsWon, score.awaySetsWon),
                    color = BatDesign.colors.textPrimary,
                    style = BatDesign.typography.bodyStrong.copy(fontFeatureSettings = "tnum"),
                )
            }
        }
    }
}

@Composable
private fun playerStyle(isWinner: Boolean): TextStyle = if (isWinner) {
    BatDesign.typography.bodyStrong
} else {
    BatDesign.typography.body
}

@BatScreenPreview
@Composable
private fun MatchRowViewPreview() {
    BatPreviewSurface {
        BatList {
            (fixtureDetailPlayedPreview.matches + fixtureDetailLongNamesPreview.matches).forEachIndexed { index, match ->
                MatchRowView(match, striped = index % 2 == 1)
            }
            MatchRowView(fixtureDetailPlayedPreview.matches.first().copy(score = null), striped = false)
        }
    }
}
