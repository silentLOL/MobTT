package at.stefanirndorfer.mobtt.core.network

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger

/** `Prod` drops Kermit's default console writer, so only [crashWriter] sees shared-code logs; other environments keep both. */
fun installLogWriters(environment: Environment, crashWriter: LogWriter) {
    if (environment == Environment.Prod) {
        Logger.setLogWriters(crashWriter)
    } else {
        Logger.addLogWriter(crashWriter)
    }
}
