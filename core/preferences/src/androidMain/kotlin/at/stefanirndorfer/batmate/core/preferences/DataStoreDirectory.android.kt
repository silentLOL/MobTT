package at.stefanirndorfer.batmate.core.preferences

import android.content.Context
import org.koin.core.scope.Scope

internal actual fun Scope.dataStoreDirectory(): String = get<Context>().filesDir.absolutePath
