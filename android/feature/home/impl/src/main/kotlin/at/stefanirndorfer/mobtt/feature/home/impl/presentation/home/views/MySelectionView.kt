package at.stefanirndorfer.mobtt.feature.home.impl.presentation.home.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatCard
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatHeroPill
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatSecondaryButton
import at.stefanirndorfer.mobtt.feature.home.impl.presentation.home.models.HomeSelectionUi

@Composable
fun MySelectionView(
    selection: HomeSelectionUi,
    onChoosePlayerClick: () -> Unit,
    onChooseClubClick: () -> Unit,
    onPlayerClick: (String) -> Unit,
    onClubClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (selection) {
        HomeSelectionUi.None -> ChoosePromptView(onChoosePlayerClick, onChooseClubClick, modifier)
        HomeSelectionUi.Unavailable -> Unit
        is HomeSelectionUi.Player -> HeroView(
            overline = stringResource(R.string.home_my_player_overline),
            name = selection.name,
            detail = "${selection.clubName} • ${selection.associationName}",
            kind = stringResource(R.string.home_favorite_kind_player),
            onClick = { onPlayerClick(selection.playerId) },
            modifier = modifier,
        )
        is HomeSelectionUi.Club -> HeroView(
            overline = stringResource(R.string.home_my_club_overline),
            name = selection.name,
            detail = selection.associationName,
            kind = stringResource(R.string.home_favorite_kind_club),
            onClick = { onClubClick(selection.clubId) },
            modifier = modifier,
        )
    }
}

@Composable
private fun HeroView(
    overline: String,
    name: String,
    detail: String,
    kind: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = listOf(kind, name, detail).joinToString()
    BatHeroCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BatDesign.sizing.minTouchTarget)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            BatHeroPill(text = overline)
            Text(
                text = name,
                color = BatDesign.colors.onHero,
                style = BatDesign.typography.display,
            )
            Text(
                text = detail,
                color = BatDesign.colors.onHero,
                style = BatDesign.typography.body,
            )
        }
    }
}

@Composable
private fun ChoosePromptView(onChoosePlayerClick: () -> Unit, onChooseClubClick: () -> Unit, modifier: Modifier = Modifier) {
    BatCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            Text(
                text = stringResource(R.string.home_choose_title),
                modifier = Modifier.semantics { heading() },
                color = BatDesign.colors.textPrimary,
                style = BatDesign.typography.title,
            )
            Text(
                text = stringResource(R.string.home_choose_body),
                color = BatDesign.colors.textSecondary,
                style = BatDesign.typography.body,
            )
            BatPrimaryButton(
                text = stringResource(R.string.home_choose_player_button),
                onClick = onChoosePlayerClick,
                modifier = Modifier.fillMaxWidth(),
            )
            BatSecondaryButton(
                text = stringResource(R.string.home_choose_club_button),
                onClick = onChooseClubClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@BatScreenPreview
@Composable
private fun MySelectionViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.lg)) {
            MySelectionView(selection = HomeSelectionUi.None, onChoosePlayerClick = {}, onChooseClubClick = {}, onPlayerClick = {}, onClubClick = {})
            MySelectionView(
                selection = HomeSelectionUi.Player("Michael Huber", "TTC Olympic", "WTTV", "p1"),
                onChoosePlayerClick = {},
                onChooseClubClick = {},
                onPlayerClick = {},
                onClubClick = {},
            )
            MySelectionView(selection = HomeSelectionUi.Club("Nussdorf TTC", "WTTV", "c1"), onChoosePlayerClick = {}, onChooseClubClick = {}, onPlayerClick = {}, onClubClick = {})
            MySelectionView(
                selection = HomeSelectionUi.Player(
                    "Katharina Habsburg-Lothringen-Esterházy",
                    "Union Tischtennisclub St. Pölten Nord-West",
                    "Niederösterreichischer Tischtennisverband (NÖTTV)",
                    "p2",
                ),
                onChoosePlayerClick = {},
                onChooseClubClick = {},
                onPlayerClick = {},
                onClubClick = {},
            )
        }
    }
}
