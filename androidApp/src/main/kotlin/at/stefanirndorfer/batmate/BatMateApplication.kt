package at.stefanirndorfer.batmate

import android.app.Application
import at.stefanirndorfer.batmate.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class BatMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@BatMateApplication)
        }
    }
}
