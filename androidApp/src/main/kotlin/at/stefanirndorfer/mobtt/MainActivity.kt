package at.stefanirndorfer.mobtt

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.stefanirndorfer.mobtt.designsystem.theme.BatTheme
import at.stefanirndorfer.mobtt.feature.settings.api.Appearance
import at.stefanirndorfer.mobtt.feature.settings.api.AppearanceRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val appearanceRepository: AppearanceRepository by inject()

    /** Links the app is opened with, buffered until the app's content collects them. */
    private val incomingLinks = Channel<IncomingLink>(Channel.BUFFERED)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // A recreated activity still holds the intent that started it; only a fresh start is a new link.
        if (savedInstanceState == null) {
            receiveLink(intent, coldStart = true)
        }

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
                MobTTApp(incomingLinks = incomingLinks.receiveAsFlow())
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        receiveLink(intent, coldStart = false)
    }

    private fun receiveLink(intent: Intent, coldStart: Boolean) {
        val url = intent.takeIf { it.action == Intent.ACTION_VIEW }?.dataString ?: return
        incomingLinks.trySend(IncomingLink(url, coldStart))
    }
}
