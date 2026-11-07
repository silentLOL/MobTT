package at.stefanirndorfer.mobtt.feature.settings.api

/** Where the open source licenses screen reads the generated list from; the app module provides it. */
fun interface LicensesSource {
    /** The list as AboutLibraries' JSON: the libraries the build found plus the hand-listed fonts and assets. */
    suspend fun load(): String
}
