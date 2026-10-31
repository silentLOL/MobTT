package at.stefanirndorfer.batmate.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import kotlin.math.roundToInt

private const val SHEET_MIN_HEIGHT_FRACTION = 0.75f

/** Keeps a search sheet at least three quarters of the height it may take, so it does not jump with the number of results. */
fun Modifier.sheetMinHeight(): Modifier = layout { measurable, constraints ->
    val minHeight = if (constraints.hasBoundedHeight) {
        (constraints.maxHeight * SHEET_MIN_HEIGHT_FRACTION).roundToInt()
    } else {
        constraints.minHeight
    }
    val placeable = measurable.measure(constraints.copy(minHeight = maxOf(constraints.minHeight, minHeight)))
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}
