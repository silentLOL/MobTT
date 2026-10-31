package at.stefanirndorfer.mobtt.core.analytics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Sends one `search_performed` once typing has paused, so a query is counted once and not per keystroke. */
class SearchEvents(
    private val scope: CoroutineScope,
    private val tracker: AnalyticsTracker,
    private val screen: String,
    private val settleMillis: Long = SETTLE_MILLIS,
) {
    private var pending: Job? = null

    fun onQueryChanged(query: String) {
        pending?.cancel()
        if (query.isBlank()) {
            return
        }
        pending = scope.launch {
            delay(settleMillis)
            tracker.searchPerformed(screen, query)
        }
    }

    companion object {
        const val SETTLE_MILLIS = 1_000L
    }
}
