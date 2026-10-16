package at.stefanirndorfer.batmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.text.BasicText
import at.stefanirndorfer.batmate.domain.app.GetPlatformNameUseCase
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val getPlatformName: GetPlatformNameUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            BasicText(getPlatformName())
        }
    }
}
