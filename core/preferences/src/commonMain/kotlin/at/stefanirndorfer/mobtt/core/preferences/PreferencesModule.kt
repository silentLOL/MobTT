package at.stefanirndorfer.mobtt.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val DATA_STORE_FILE_NAME = "batmate.preferences_pb"

val preferencesModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { "${dataStoreDirectory()}/$DATA_STORE_FILE_NAME".toPath() },
        )
    }
}

internal expect fun Scope.dataStoreDirectory(): String
