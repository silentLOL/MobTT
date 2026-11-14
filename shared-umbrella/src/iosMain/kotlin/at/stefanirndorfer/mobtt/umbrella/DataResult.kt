package at.stefanirndorfer.mobtt.umbrella

import at.stefanirndorfer.mobtt.domain.club.DataUnavailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * A thrown [DataUnavailable] reaches Swift as an `NSError` that no `catch is DataUnavailable` matches,
 * and an error inside a Flow ends the app. Calls that can fail this way return one of these instead,
 * matched with `onEnum(of:)`.
 */
sealed interface DataResult<out T> {
    data class Success<out T>(val value: T) : DataResult<T>

    data class Unavailable(val error: DataUnavailable) : DataResult<Nothing>
}

internal fun <T> Flow<T>.asDataResult(): Flow<DataResult<T>> = map<T, DataResult<T>> { DataResult.Success(it) }
    .catch { e ->
        if (e is DataUnavailable) {
            emit(DataResult.Unavailable(e))
        } else {
            throw e
        }
    }

internal suspend fun <T> dataResult(block: suspend () -> T): DataResult<T> = try {
    DataResult.Success(block())
} catch (e: DataUnavailable) {
    DataResult.Unavailable(e)
}
