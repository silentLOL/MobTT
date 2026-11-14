package at.stefanirndorfer.mobtt.core.ui.section

import at.stefanirndorfer.mobtt.core.ui.RetryTrigger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

/** Loads a section with [load] straight away, and again after every retry. Any failure makes it unavailable. */
fun <T> RetryTrigger.section(load: suspend () -> T): Flow<SectionUi<T>> = restarting {
    flow {
        emit(SectionUi.Loading)
        emit(loadSection(load))
    }
}

/**
 * Waits until [expanded] first turns true, then loads with [load] and keeps the result when the section is closed
 * again. A retry reloads it only once it has been opened.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> RetryTrigger.lazySection(expanded: Flow<Boolean>, load: suspend () -> T): Flow<SectionUi<T>> = expanded
    .filter { it }
    .take(1)
    .flatMapLatest { section(load) }
    .onStart { emit(SectionUi.Loading) }

private suspend fun <T> loadSection(load: suspend () -> T): SectionUi<T> = try {
    SectionUi.Loaded(load())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    SectionUi.Unavailable
}
