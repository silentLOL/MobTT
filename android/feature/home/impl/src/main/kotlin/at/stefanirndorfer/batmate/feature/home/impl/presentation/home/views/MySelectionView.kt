package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.views

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
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatHeroPill
import at.stefanirndorfer.batmate.designsystem.ui.components.BatPrimaryButton
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.models.HomeSelectionUi

@Composable
fun MySelectionView(
    selection: HomeSelectionUi,
    onChooseClick: () -> Unit,
    onPlayerClick: (String) -> Unit,
    onClubClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (selection) {
        HomeSelectionUi.None -> ChoosePromptView(onChooseClick = onChooseClick, modifier = modifier)
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
private fun ChoosePromptView(onChooseClick: () -> Unit, modifier: Modifier = Modifier) {
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
                text = stringResource(R.string.home_choose_button),
                onClick = onChooseClick,
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
            MySelectionView(selection = HomeSelectionUi.None, onChooseClick = {}, onPlayerClick = {}, onClubClick = {})
            MySelectionView(
                selection = HomeSelectionUi.Player("Michael Huber", "TTC Olympic", "WTTV", "p1"),
                onChooseClick = {},
                onPlayerClick = {},
                onClubClick = {},
            )
            MySelectionView(selection = HomeSelectionUi.Club("Nussdorf TTC", "WTTV", "c1"), onChooseClick = {}, onPlayerClick = {}, onClubClick = {})
            MySelectionView(
                selection = HomeSelectionUi.Player(
                    "Katharina Habsburg-Lothringen-Esterházy",
                    "Union Tischtennisclub St. Pölten Nord-West",
                    "Niederösterreichischer Tischtennisverband (NÖTTV)",
                    "p2",
                ),
                onChooseClick = {},
                onPlayerClick = {},
                onClubClick = {},
            )
        }
    }
}
