package at.stefanirndorfer.batmate.designsystem.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class BatShadow(internal val elevation: Dp) {
    SM(1.dp),
    MD(4.dp),
    LG(8.dp),
    XL(16.dp),
}

/** [tint] colours the shadow, e.g. the brand colour under hero cards. */
fun Modifier.batShadow(
    shadow: BatShadow,
    shape: Shape,
    tint: Color = Color.Black,
): Modifier = shadow(
    elevation = shadow.elevation,
    shape = shape,
    ambientColor = tint,
    spotColor = tint,
)
