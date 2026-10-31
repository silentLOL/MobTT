package at.stefanirndorfer.mobtt

import android.app.Application
import at.stefanirndorfer.mobtt.core.di.initKoin
import at.stefanirndorfer.mobtt.core.network.AppConfig
import at.stefanirndorfer.mobtt.core.network.Environment
import at.stefanirndorfer.mobtt.feature.club.impl.di.clubModule
import at.stefanirndorfer.mobtt.feature.home.impl.di.homeModule
import at.stefanirndorfer.mobtt.feature.players.impl.di.playersModule
import at.stefanirndorfer.mobtt.feature.settings.impl.di.settingsModule
import org.koin.android.ext.koin.androidContext

class MobTTApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = AppConfig(
            Environment.fromKey(BuildConfig.ENVIRONMENT),
            BuildConfig.VERSION_NAME,
            BuildConfig.XTTV_API_KEY,
        )
        initKoin(config) {
            androidContext(this@MobTTApplication)
            modules(directoryModules + listOf(clubModule, homeModule, playersModule, settingsModule))
        }
    }
}
