package at.stefanirndorfer.mobtt.core.analytics.di

import android.content.Context
import at.stefanirndorfer.mobtt.core.analytics.AnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.CrashReporter
import at.stefanirndorfer.mobtt.core.analytics.FirebaseAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.FirebaseCrashReporter
import at.stefanirndorfer.mobtt.core.analytics.NoOpAnalyticsTracker
import at.stefanirndorfer.mobtt.core.analytics.NoOpCrashReporter
import at.stefanirndorfer.mobtt.core.analytics.UsageConsentCollection
import com.google.firebase.FirebaseApp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Binds the Firebase implementations when the build carries a `google-services.json` (the SDK is then initialised),
 * and the no-ops otherwise. [analyticsEnabled] is false in `deb`, which still reports crashes.
 */
fun analyticsModule(analyticsEnabled: Boolean) = module {
    single<AnalyticsTracker> {
        val context = get<Context>()
        if (context.firebaseConfigured() && analyticsEnabled) {
            FirebaseAnalyticsTracker(context)
        } else {
            NoOpAnalyticsTracker
        }
    }
    single<CrashReporter> {
        if (get<Context>().firebaseConfigured()) {
            FirebaseCrashReporter()
        } else {
            NoOpCrashReporter
        }
    }
    singleOf(::UsageConsentCollection)
}

private fun Context.firebaseConfigured() = FirebaseApp.getApps(this).isNotEmpty()
