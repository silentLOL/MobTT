package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import at.stefanirndorfer.batmate.designsystem.icon.BatIcon
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.theme.BatShadow
import at.stefanirndorfer.batmate.designsystem.theme.batShadow

@Composable
fun BatLogoMark(modifier: Modifier = Modifier) {
    val colors = BatDesign.colors
    val shape = BatDesign.shapes.medium
    Box(
        modifier = modifier
            .size(BatDesign.sizing.logo)
            .batShadow(BatShadow.MD, shape, tint = colors.logoStart)
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.logoStart, colors.logoEnd))),
        contentAlignment = Alignment.Center,
    ) {
        BatIconView(icon = BatIcon.Logo, contentDescription = null, tint = colors.onHero, size = BatDesign.sizing.iconLarge)
    }
}

/** The chevron at the top left that returns to the previous screen. */
class BatTopBarBack(val contentDescription: String, val onClick: () -> Unit)

@Composable
fun BatTopBar(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    subtitle: String? = null,
    back: BatTopBarBack? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = BatDesign.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BatDesign.spacing.lg, vertical = BatDesign.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            if (back != null) {
                BatIconButton(icon = BatIcon.ArrowBack, contentDescription = back.contentDescription, onClick = back.onClick)
            }
            BatLogoMark()
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.sm),
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f, fill = false),
                        color = colors.textPrimary,
                        style = BatDesign.typography.subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (badge != null) {
                        BatBadge(text = badge)
                    }
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = colors.textSecondary,
                        style = BatDesign.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            actions()
        }
        HorizontalDivider(color = colors.border)
    }
}

@BatScreenPreview
@Composable
private fun BatTopBarPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatTopBar(
                title = "MobTT",
                badge = "Pro",
                subtitle = "Vienna Table Tennis Association (WTTV)",
                actions = {
                    BatIconButton(icon = BatIcon.DarkMode, contentDescription = "Dark mode", onClick = {})
                    BatIconButton(icon = BatIcon.Settings, contentDescription = "Settings", onClick = {})
                },
            )
            BatTopBar(title = "MobTT")
            BatTopBar(title = "MobTT", back = BatTopBarBack("Back", onClick = {}))
        }
    }
}

@BatScreenPreview
@Composable
private fun BatTopBarLongContentPreview() {
    BatPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.md)) {
            BatTopBar(
                title = "MobTT Championship Overview",
                badge = "Pro",
                subtitle = "Lower Austrian Table Tennis Association, Division 1 West",
                actions = {
                    BatIconButton(icon = BatIcon.Settings, contentDescription = "Settings", onClick = {})
                },
            )
            BatTopBar(title = "MobTT", badge = "Beta")
        }
    }
}
