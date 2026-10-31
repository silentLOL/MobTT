package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import at.stefanirndorfer.batmate.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign

/** Frames [BatListRow]s and [BatListItem]s as one list: a single rounded, bordered block with no gaps between rows. */
@Composable
fun BatList(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = BatDesign.shapes.large,
        color = BatDesign.colors.surface,
        border = BorderStroke(BatDesign.sizing.borderThin, BatDesign.colors.border),
    ) {
        Column(content = content)
    }
}

/**
 * The frame of one [BatList] row with free [content]. Pass `striped = true` on every other row for the darker
 * band. [highlighted] sets the row in a brand-coloured frame inside its band, so it stands out from both bands.
 * Pass [onClick] to make the whole row the target, or leave it out when the content holds its own targets.
 * [contentDescription] replaces what the content reads out.
 */
@Composable
fun BatListItem(
    modifier: Modifier = Modifier,
    striped: Boolean = false,
    highlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
    content: @Composable () -> Unit,
) {
    val colors = BatDesign.colors
    val band = if (striped) {
        colors.surfaceMuted
    } else {
        colors.surface
    }
    val inner: @Composable () -> Unit = {
        val semanticsModifier = if (contentDescription != null) {
            Modifier.clearAndSetSemantics { this.contentDescription = contentDescription }
        } else if (onClick != null) {
            Modifier.semantics(mergeDescendants = true) {}
        } else {
            Modifier
        }
        Box(
            modifier = semanticsModifier
                .fillMaxWidth()
                .heightIn(min = BatDesign.sizing.minTouchTarget)
                .padding(horizontal = BatDesign.spacing.md, vertical = BatDesign.spacing.sm),
            contentAlignment = Alignment.CenterStart,
        ) { content() }
    }
    val frameModifier = if (highlighted) {
        Modifier.padding(BatDesign.spacing.xs)
    } else {
        Modifier
    }
    val shape = if (highlighted) {
        BatDesign.shapes.medium
    } else {
        RectangleShape
    }
    val color = if (highlighted) {
        colors.brandContainer
    } else {
        band
    }
    val border = if (highlighted) {
        BorderStroke(BatDesign.sizing.borderFocused, colors.brand)
    } else {
        null
    }
    Box(modifier = modifier.fillMaxWidth().background(band)) {
        if (onClick != null) {
            Surface(onClick = onClick, modifier = frameModifier.fillMaxWidth(), shape = shape, color = color, border = border, content = inner)
        } else {
            Surface(modifier = frameModifier.fillMaxWidth(), shape = shape, color = color, border = border, content = inner)
        }
    }
}

/**
 * A row of a [BatList] with a [title], an optional [leadingText] (e.g. a rank), [supportingText] and [badge]
 * below the title, and a [trailingText] that lines up in a column across rows.
 */
@Composable
fun BatListRow(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    leadingText: String? = null,
    supportingText: String? = null,
    trailingText: String? = null,
    badge: String? = null,
    striped: Boolean = false,
    highlighted: Boolean = false,
    contentDescription: String? = null,
) {
    val colors = BatDesign.colors
    BatListItem(
        modifier = modifier,
        striped = striped,
        highlighted = highlighted,
        onClick = onClick,
        contentDescription = contentDescription,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(BatDesign.spacing.md), verticalAlignment = Alignment.CenterVertically) {
            leadingText?.let {
                Text(
                    text = it,
                    modifier = Modifier.widthIn(min = BatDesign.sizing.listLeadingColumn),
                    color = colors.textSecondary,
                    style = BatDesign.typography.bodyStrong.copy(fontFeatureSettings = "tnum"),
                    textAlign = TextAlign.Center,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BatDesign.spacing.xs)) {
                Text(
                    text = title,
                    color = colors.textPrimary,
                    style = BatDesign.typography.bodyStrong,
                    overflow = TextOverflow.Ellipsis,
                )
                supportingText?.let {
                    Text(text = it, color = colors.textSecondary, style = BatDesign.typography.caption)
                }
                badge?.let { BatBadge(text = it) }
            }
            trailingText?.let {
                Text(
                    text = it,
                    modifier = Modifier.widthIn(min = BatDesign.sizing.listTrailingColumn),
                    color = colors.textSecondary,
                    style = BatDesign.typography.caption.copy(fontFeatureSettings = "tnum"),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun BatListPreview() {
    BatPreviewSurface {
        BatList {
            listOf(
                "Katharina Huber" to "RC 1850",
                "Max Mustermann" to "RC 1720",
                "Katharina Habsburg-Lothringen-Esterházy" to "RC 1604",
                "Ohne RC" to null,
            ).forEachIndexed { index, (name, rc) ->
                BatListRow(title = name, trailingText = rc, striped = index % 2 == 1, onClick = {})
            }
            BatListRow(
                title = "TTC Olympic 1",
                leadingText = "2",
                supportingText = "6 played · 9 pts",
                badge = "This Team",
                highlighted = true,
            )
        }
    }
}
