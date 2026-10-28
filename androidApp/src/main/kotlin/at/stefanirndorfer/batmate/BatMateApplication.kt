package at.stefanirndorfer.batmate

import android.app.Application
import at.stefanirndorfer.batmate.core.di.initKoin
import at.stefanirndorfer.batmate.core.network.AppConfig
import at.stefanirndorfer.batmate.core.network.Environment
import at.stefanirndorfer.batmate.feature.club.impl.di.clubModule
import at.stefanirndorfer.batmate.feature.home.impl.di.homeModule
import at.stefanirndorfer.batmate.feature.settings.impl.di.settingsModule
import org.koin.android.ext.koin.androidContext

class BatMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = AppConfig(
            Environment.fromKey(BuildConfig.ENVIRONMENT),
            BuildConfig.VERSION_NAME,
            BuildConfig.XTTV_API_KEY,
        )
        initKoin(config) {
            androidContext(this@BatMateApplication)
            modules(directoryModules + listOf(clubModule, homeModule, settingsModule))
        }
    }
}
