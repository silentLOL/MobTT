package at.stefanirndorfer.batmate.feature.club.impl.presentation.club.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatCard
import at.stefanirndorfer.batmate.domain.club.LineupPlayer
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewLongNamesPreview
import at.stefanirndorfer.batmate.feature.club.impl.presentation.club.preview.clubOverviewPreview

private val RC_COLUMN_MIN_WIDTH = 64.dp

/** The whole card opens the Player; the RC values line up in a column. */
@Composable
fun PlayerCardView(player: LineupPlayer, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BatCard(modifier = modifier.fillMaxWidth(), onClick = onClick, contentPadding = PaddingValues(BatDesign.spacing.md)) {
        Row(
            modifier = Modifier
                .heightIn(min = BatDesign.sizing.minTouchTarget)
                .semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            Text(
                text = player.name,
                modifier = Modifier.weight(1f),
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.bodyStrong,
            )
            player.rcPoints?.let { rc ->
                Text(
                    text = stringResource(R.string.team_detail_lineup_rc_label, rc),
                    modifier = Modifier.widthIn(min = RC_COLUMN_MIN_WIDTH),
                    color = BatDesign.colors.textSecondary,
                    style = BatDesign.typography.caption.copy(fontFeatureSettings = "tnum"),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun PlayerCardViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            (clubOverviewPreview.players + clubOverviewLongNamesPreview.players).forEach { PlayerCardView(it, onClick = {}) }
        }
    }
}
