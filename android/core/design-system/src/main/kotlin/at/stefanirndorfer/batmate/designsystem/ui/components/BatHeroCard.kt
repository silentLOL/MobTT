package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import at.stefanirndorfer.batmate.designsystem.theme.BatShadow
import at.stefanirndorfer.batmate.designsystem.theme.batShadow

@Composable
fun BatHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = BatDesign.colors
    val shape = BatDesign.shapes.extraLarge
    Column(
        modifier = modifier
            .batShadow(BatShadow.XL, shape, tint = colors.heroStart)
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.heroStart, colors.heroEnd)))
            .padding(BatDesign.spacing.xxl),
        content = content,
    )
}

@Composable
fun BatHeroPill(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier
            .clip(BatDesign.shapes.full)
            .background(BatDesign.colors.onHero.copy(alpha = 0.2f))
            .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.xs),
        color = BatDesign.colors.onHero,
        style = BatDesign.typography.overline,
    )
}

@Composable
fun BatHeroStatRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.padding(top = BatDesign.spacing.lg)) {
        HorizontalDivider(color = BatDesign.colors.onHero.copy(alpha = 0.15f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = BatDesign.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md),
        ) {
            content()
        }
    }
}

@BatScreenPreview
@Composable
private fun BatHeroCardPreview() {
    BatPreviewSurface {
        BatHeroCard(modifier = Modifier.fillMaxWidth()) {
            BatHeroPill(text = "Active player")
            Text(
                text = "Michael Huber",
                modifier = Modifier.padding(top = BatDesign.spacing.sm),
                color = BatDesign.colors.onHero,
                style = BatDesign.typography.display,
            )
            Text(
                text = "TTC Olympic • WTTV",
                color = BatDesign.colors.onHeroMuted,
                style = BatDesign.typography.body,
            )
            BatHeroStatRow {
                BatStatView(label = "QTTR points", value = "1842", onHero = true, modifier = Modifier.weight(1f))
                BatStatView(label = "ÖTTV rank", value = "#45", onHero = true, modifier = Modifier.weight(1f))
                BatStatView(label = "Singles", value = "34:12", onHero = true, modifier = Modifier.weight(1f))
            }
        }
    }
}
