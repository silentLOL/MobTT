package at.stefanirndorfer.batmate.designsystem.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import at.stefanirndorfer.batmate.designsystem.theme.BatDesign
import androidx.compose.material3.Text
import at.stefanirndorfer.batmate.designsystem.preview.BatScreenPreview
import at.stefanirndorfer.batmate.designsystem.theme.BatTheme

/** Content is capped at [at.stefanirndorfer.batmate.designsystem.theme.BatSizing.maxContentWidth] and centred on wide screens. */
@Composable
fun BatScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.batContentBackground(),
        containerColor = Color.Transparent,
        contentColor = BatDesign.colors.textPrimary,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(Modifier.widthIn(max = BatDesign.sizing.maxContentWidth)) {
                content(padding)
            }
        }
    }
}

@BatScreenPreview
@Composable
private fun BatScreenScaffoldPreview() {
    BatTheme {
        BatScreenScaffold(
            topBar = { BatTopBar(title = "Bat Mate") },
        ) { padding ->
            Text(text = "Content", modifier = Modifier.padding(padding))
        }
    }
}
