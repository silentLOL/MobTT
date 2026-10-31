package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import at.stefanirndorfer.mobtt.designsystem.icon.BatIcon
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign

/** [contentDescription] names the action a tap performs; [stateDescription] names what the star shows now. */
@Composable
fun BatFavoriteButton(
    isFavorite: Boolean,
    contentDescription: String,
    stateDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BatIconButton(
        icon = if (isFavorite) BatIcon.StarFilled else BatIcon.Star,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier.semantics { this.stateDescription = stateDescription },
        tint = if (isFavorite) BatDesign.colors.brand else BatDesign.colors.textSecondary,
    )
}

@BatScreenPreview
@Composable
private fun BatFavoriteButtonPreview() {
    BatPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatFavoriteButton(
                isFavorite = false,
                contentDescription = "Add to Favorites",
                stateDescription = "Not a Favorite",
                onClick = {},
            )
            BatFavoriteButton(
                isFavorite = true,
                contentDescription = "Remove from Favorites",
                stateDescription = "Favorite",
                onClick = {},
            )
        }
    }
}
