package at.stefanirndorfer.mobtt.core.analytics

import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics

internal class FirebaseAnalyticsTracker(context: Context) : AnalyticsTracker {
    private val analytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(name: String, params: Map<String, Any>) {
        analytics.logEvent(name, bundleOf(*params.map { it.key to it.value }.toTypedArray()))
    }

    override fun setCollectionEnabled(enabled: Boolean) {
        analytics.setAnalyticsCollectionEnabled(enabled)
    }
}
