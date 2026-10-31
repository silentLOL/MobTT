package at.stefanirndorfer.mobtt.feature.home.impl.presentation.home.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import at.stefanirndorfer.mobtt.core.localization.R
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatAvatarTint
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatCard
import at.stefanirndorfer.mobtt.designsystem.ui.components.BatInitialsAvatar
import at.stefanirndorfer.mobtt.domain.user.Favorite
import at.stefanirndorfer.mobtt.domain.user.ResolvedFavorite
import at.stefanirndorfer.mobtt.feature.home.impl.presentation.home.formatting.initialsOf

@Composable
fun FavoriteRowView(
    favorite: ResolvedFavorite,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val tint = when (favorite.favorite) {
        is Favorite.Club -> BatAvatarTint.LIVE
        is Favorite.Player, is Favorite.Team -> BatAvatarTint.BRAND
    }
    val kind = stringResource(
        when (favorite.favorite) {
            is Favorite.Player -> R.string.home_favorite_kind_player
            is Favorite.Club -> R.string.home_favorite_kind_club
            is Favorite.Team -> R.string.home_favorite_kind_team
        },
    )
    val rowDescription = listOfNotNull(kind, favorite.name, favorite.detail).joinToString()
    BatCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(BatDesign.spacing.md)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            BatInitialsAvatar(initials = initialsOf(favorite.name), tint = tint)
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (onClick != null) {
                            Modifier
                                .heightIn(min = BatDesign.sizing.minTouchTarget)
                                .clickable(role = Role.Button, onClick = onClick)
                                .semantics { contentDescription = rowDescription }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                Text(text = favorite.name, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
                favorite.detail?.let { detail ->
                    Text(text = detail, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
                }
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun FavoriteRowViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            FavoriteRowView(ResolvedFavorite(Favorite.Player("p"), "Anna Gruber", "TTC Olympic"), onClick = {})
            FavoriteRowView(ResolvedFavorite(Favorite.Club("c"), "Nussdorf TTC", "WTTV"), onClick = {})
            FavoriteRowView(ResolvedFavorite(Favorite.Team("t"), "TTC Olympic 1", "TTC Olympic"), onClick = {})
            FavoriteRowView(
                ResolvedFavorite(
                    Favorite.Team("lt"),
                    "Union Tischtennisclub St. Pölten Nord-West 1",
                    "Union Tischtennisclub St. Pölten Nord-West",
                ),
                onClick = {},
            )
            FavoriteRowView(
                ResolvedFavorite(
                    Favorite.Player("l"),
                    "Katharina Habsburg-Lothringen-Esterházy",
                    "Union Tischtennisclub St. Pölten Nord-West",
                ),
                onClick = {},
            )
        }
    }
}
