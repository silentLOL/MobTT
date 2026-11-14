package at.stefanirndorfer.mobtt

import android.content.Context
import at.stefanirndorfer.mobtt.feature.settings.api.LicensesSource
import org.koin.dsl.module

/** Reads the licenses list the AboutLibraries plugin generates into this app's raw resources. */
val licensesSourceModule = module {
    single<LicensesSource> {
        val context = get<Context>()
        LicensesSource { context.resources.openRawResource(R.raw.aboutlibraries).bufferedReader().use { it.readText() } }
    }
}
