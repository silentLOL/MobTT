package at.stefanirndorfer.mobtt.designsystem.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import at.stefanirndorfer.mobtt.designsystem.preview.BatPreviewSurface
import at.stefanirndorfer.mobtt.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.mobtt.designsystem.theme.BatDesign
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

/** One point of a [BatRatingChart]: [value] drawn as the line, [low] to [high] as the band around it. */
@Immutable
data class BatChartPoint(val x: Float, val value: Float, val low: Float, val high: Float)

/** Most points that still get a dot each; above it, only the line and band are drawn. */
const val BAT_CHART_MAX_DOTS = 40

/**
 * A line through [points] with a shaded band from each point's low to high, ordered by x. The vertical scale fits the
 * band with some room above and below; [xLabel] names the first and last x. With [showDots], each point gets a dot.
 * Tapping or dragging reports the nearest point's index to [onSelect], and [selectedIndex] is marked with a rule. The
 * chart is read as one element, [contentDescription]; with [previousLabel] and [nextLabel], screen readers step through
 * the points with custom actions, which report to [onSelect] too.
 */
@Composable
fun BatRatingChart(
    points: List<BatChartPoint>,
    xLabel: (Float) -> String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    showDots: Boolean = points.size <= BAT_CHART_MAX_DOTS,
    selectedIndex: Int? = null,
    onSelect: ((Int) -> Unit)? = null,
    previousLabel: String? = null,
    nextLabel: String? = null,
) {
    val colors = BatDesign.colors
    val sizing = BatDesign.sizing
    val spacing = BatDesign.spacing
    val labelStyle = BatDesign.typography.caption.copy(color = colors.textTertiary)
    val measurer = rememberTextMeasurer()
    val scale = remember(points) { BatChartScale.of(points) }
    // The plot's left and right edge, as last drawn, so a touch can be mapped to the nearest point.
    val plotEdges = remember { floatArrayOf(0f, 0f) }
    val currentOnSelect by rememberUpdatedState(onSelect)
    fun nearest(touchX: Float): Int? {
        if (points.isEmpty()) {
            return null
        }
        val minX = points.first().x
        val spanX = points.last().x - minX
        return points.indices.minByOrNull { index ->
            val x = if (spanX == 0f) {
                (plotEdges[0] + plotEdges[1]) / 2
            } else {
                plotEdges[0] + (points[index].x - minX) / spanX * (plotEdges[1] - plotEdges[0])
            }
            abs(x - touchX)
        }
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(sizing.chartHeight)
            .semantics {
                this.contentDescription = contentDescription
                if (onSelect != null && previousLabel != null && nextLabel != null && points.isNotEmpty()) {
                    // Without a selection, both start from the latest point.
                    val current = selectedIndex ?: points.size
                    customActions = listOf(
                        CustomAccessibilityAction(previousLabel) {
                            (current - 1).takeIf { it >= 0 }?.let { onSelect(it) } != null
                        },
                        CustomAccessibilityAction(nextLabel) {
                            val next = selectedIndex?.plus(1) ?: points.lastIndex
                            next.takeIf { it <= points.lastIndex }?.let { onSelect(it) } != null
                        },
                    )
                }
            }
            .pointerInput(points) {
                detectTapGestures { offset -> nearest(offset.x)?.let { currentOnSelect?.invoke(it) } }
            }
            .pointerInput(points) {
                detectHorizontalDragGestures { change, _ -> nearest(change.position.x)?.let { currentOnSelect?.invoke(it) } }
            },
    ) {
        if (points.isEmpty()) {
            return@Canvas
        }
        val yLabels = scale.ticks.map { measurer.measure(it.toInt().toString(), labelStyle) }
        val xTexts = listOf(points.first().x, points.last().x).distinct().map { measurer.measure(xLabel(it), labelStyle) }
        val gap = spacing.sm.toPx()
        val left = (yLabels.maxOfOrNull { it.size.width } ?: 0) + gap
        val bottom = size.height - (xTexts.maxOfOrNull { it.size.height } ?: 0) - gap
        val top = sizing.chartDot.toPx()
        val right = size.width - sizing.chartDot.toPx()
        val minX = points.first().x
        val spanX = points.last().x - minX
        plotEdges[0] = left
        plotEdges[1] = right

        fun xOf(x: Float) = if (spanX == 0f) {
            (left + right) / 2
        } else {
            left + (x - minX) / spanX * (right - left)
        }
        fun yOf(value: Float) = bottom - (value - scale.min) / (scale.max - scale.min) * (bottom - top)

        scale.ticks.forEachIndexed { index, tick ->
            val y = yOf(tick)
            drawLine(colors.border, Offset(left, y), Offset(right, y), strokeWidth = sizing.borderThin.toPx())
            val label = yLabels[index]
            drawText(label, topLeft = Offset(left - gap - label.size.width, y - label.size.height / 2))
        }
        drawXLabels(xTexts, left, right, bottom + gap)

        if (points.size == 1) {
            val point = points.single()
            drawLine(colors.brandContainer, Offset(xOf(point.x), yOf(point.high)), Offset(xOf(point.x), yOf(point.low)), strokeWidth = sizing.chartDot.toPx(), cap = StrokeCap.Round)
        } else {
            val band = Path().apply {
                moveTo(xOf(points.first().x), yOf(points.first().high))
                points.drop(1).forEach { lineTo(xOf(it.x), yOf(it.high)) }
                points.asReversed().forEach { lineTo(xOf(it.x), yOf(it.low)) }
                close()
            }
            drawPath(band, colors.brandContainer)
            val line = Path().apply {
                moveTo(xOf(points.first().x), yOf(points.first().value))
                points.drop(1).forEach { lineTo(xOf(it.x), yOf(it.value)) }
            }
            drawPath(line, colors.brand, style = Stroke(width = sizing.chartLine.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        if (showDots) {
            points.forEach { drawCircle(colors.brand, radius = sizing.chartDot.toPx() / 2, center = Offset(xOf(it.x), yOf(it.value))) }
        }
        points.getOrNull(selectedIndex ?: -1)?.let { point ->
            val x = xOf(point.x)
            drawLine(colors.borderStrong, Offset(x, top), Offset(x, bottom), strokeWidth = sizing.borderThin.toPx())
            drawCircle(colors.surface, radius = sizing.chartDot.toPx(), center = Offset(x, yOf(point.value)))
            drawCircle(colors.brand, radius = sizing.chartDot.toPx() * 2 / 3, center = Offset(x, yOf(point.value)))
        }
    }
}

private fun DrawScope.drawXLabels(labels: List<TextLayoutResult>, left: Float, right: Float, y: Float) {
    when (labels.size) {
        1 -> drawText(labels.single(), topLeft = Offset((left + right - labels.single().size.width) / 2, y))
        2 -> {
            drawText(labels.first(), topLeft = Offset(left, y))
            drawText(labels.last(), topLeft = Offset(right - labels.last().size.width, y))
        }
    }
}

/** The vertical range of a chart and its gridline values, rounded to a step of 1, 2 or 5 times a power of ten. */
internal class BatChartScale(val min: Float, val max: Float, val ticks: List<Float>) {
    companion object {
        private const val PADDING = 0.1f
        private const val MIN_PADDING = 10f
        private const val TICK_COUNT = 3

        fun of(points: List<BatChartPoint>): BatChartScale {
            if (points.isEmpty()) {
                return BatChartScale(0f, 1f, emptyList())
            }
            val low = points.minOf { it.low }
            val high = points.maxOf { it.high }
            val padding = max((high - low) * PADDING, MIN_PADDING)
            val min = low - padding
            val max = high + padding
            val step = niceStep((max - min) / TICK_COUNT)
            val ticks = generateSequence(ceil(min / step) * step) { it + step }.takeWhile { it <= max }.toList()
            return BatChartScale(min, max, ticks)
        }

        private fun niceStep(raw: Float): Float {
            val magnitude = 10f.pow(floor(log10(raw)))
            val nice = when (raw / magnitude) {
                in 0f..1.5f -> 1f
                in 1.5f..3f -> 2f
                in 3f..7f -> 5f
                else -> 10f
            }
            return nice * magnitude
        }
    }
}

private fun previewPoints(count: Int) = List(count) { index ->
    val value = 900f + index * 12f + (index % 5) * 20f
    val deviation = max(160f - index * 4f, 60f)
    BatChartPoint(index.toFloat(), value, value - deviation, value + deviation)
}

@BatScreenPreview
@Composable
private fun BatRatingChartPreview() {
    BatPreviewSurface {
        Column {
            BatRatingChart(previewPoints(12), xLabel = { "Week ${it.toInt() + 1}" }, contentDescription = "Rating history", selectedIndex = 4)
            BatRatingChart(previewPoints(1), xLabel = { "Week ${it.toInt() + 1}" }, contentDescription = "Rating history")
            BatRatingChart(previewPoints(80), xLabel = { "Week ${it.toInt() + 1}" }, contentDescription = "Rating history")
        }
    }
}
