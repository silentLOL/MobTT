package at.stefanirndorfer.batmate.feature.home.impl.presentation.home.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import at.stefanirndorfer.batmate.core.localization.R
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.ui.components.BatAvatarTint
import at.stefanirndorfer.batmate.designsystem.ui.components.BatCard
import at.stefanirndorfer.batmate.designsystem.ui.components.BatIconButton
import at.stefanirndorfer.batmate.designsystem.ui.components.BatInitialsAvatar
import at.stefanirndorfer.batmate.domain.user.Favorite
import at.stefanirndorfer.batmate.domain.user.ResolvedFavorite
import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.formatting.initialsOf

@Composable
fun FavoriteRowView(
    favorite: ResolvedFavorite,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val tint = when (favorite.favorite) {
        is Favorite.Club -> BatAvatarTint.LIVE
        is Favorite.Player, is Favorite.Team -> BatAvatarTint.BRAND
    }
    BatCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(BatDesign.spacing.md)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            BatInitialsAvatar(initials = initialsOf(favorite.name), tint = tint)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
            ) {
                Text(text = favorite.name, color = BatDesign.colors.textPrimary, style = BatDesign.typography.bodyStrong)
                favorite.detail?.let { detail ->
                    Text(text = detail, color = BatDesign.colors.textSecondary, style = BatDesign.typography.caption)
                }
            }
            BatIconButton(
                icon = BatIcon.Close,
                contentDescription = stringResource(R.string.home_favorite_remove_cd, favorite.name),
                onClick = onRemoveClick,
            )
        }
    }
}

@BatScreenPreview
@Composable
private fun FavoriteRowViewPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm)) {
            FavoriteRowView(ResolvedFavorite(Favorite.Player("p"), "Anna Gruber", "TTC Olympic"), onRemoveClick = {})
            FavoriteRowView(ResolvedFavorite(Favorite.Club("c"), "Nussdorf TTC", "WTTV"), onRemoveClick = {}, onClick = {})
            FavoriteRowView(
                ResolvedFavorite(
                    Favorite.Player("l"),
                    "Katharina Habsburg-Lothringen-Esterházy",
                    "Union Tischtennisclub St. Pölten Nord-West",
                ),
                onRemoveClick = {},
            )
        }
    }
}
