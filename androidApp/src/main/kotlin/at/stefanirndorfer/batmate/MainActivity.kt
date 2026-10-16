package at.stefanirndorfer.batmate

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.batmate.designsystem.theme.BatTheme
import at.stefanirndorfer.batmate.feature.settings.api.Appearance
import at.stefanirndorfer.batmate.feature.settings.api.AppearanceRepository
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val appearanceRepository: AppearanceRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val appearance by appearanceRepository.observeAppearance()
                .collectAsStateWithLifecycle(initialValue = Appearance.SYSTEM)
            val darkTheme = when (appearance) {
                Appearance.SYSTEM -> isSystemInDarkTheme()
                Appearance.LIGHT -> false
                Appearance.DARK -> true
            }
            DisposableEffect(darkTheme) {
                val barStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
                onDispose {}
            }
            BatTheme(darkTheme = darkTheme) {
                BatMateApp()
            }
        }
    }
}
