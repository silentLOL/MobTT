package at.stefanirndorfer.batmate.core.ui

import at.stefanirndorfer.batmate.domain.club.DataUnavailable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest

/** Lets a ViewModel restart its streams when the user taps Retry. */
class RetryTrigger {
    private val count = MutableStateFlow(0)

    fun retry() {
        count.value++
    }

    /** Collects the flow from [block], and collects a fresh one after every [retry]. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> restarting(block: () -> Flow<T>): Flow<T> = count.flatMapLatest { block() }
}

/** Falls back to [fallback] and ends the stream when XTTV is unreachable and nothing is cached. */
fun <T> Flow<T>.catchUnavailable(fallback: T): Flow<T> = catch { e ->
    if (e is DataUnavailable) {
        emit(fallback)
    } else {
        throw e
    }
}
