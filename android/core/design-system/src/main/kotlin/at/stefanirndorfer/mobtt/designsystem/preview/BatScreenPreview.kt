package at.stefanirndorfer.mobtt.designsystem.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light", group = "Bat", showBackground = true)
@Preview(
    name = "Dark",
    group = "Bat",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Preview(name = "Small phone", group = "Bat", showBackground = true, device = "spec:width=320dp,height=568dp")
@Preview(name = "Narrow", group = "Bat", showBackground = true, device = "spec:width=240dp,height=640dp")
@Preview(name = "Tablet", group = "Bat", showBackground = true, device = "id:pixel_tablet")
@Preview(name = "Font scale 200%", group = "Bat", showBackground = true, fontScale = 2f)
annotation class BatScreenPreview
