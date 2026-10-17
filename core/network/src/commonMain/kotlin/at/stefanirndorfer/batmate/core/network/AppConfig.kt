package at.stefanirndorfer.batmate.core.network

/** [appVersion] is the app's user-visible version; it goes out in the `User-Agent` XTTV sees. */
data class AppConfig(val environment: Environment, val appVersion: String) {
    /** Every environment that talks to XTTV reads production; XTTV has no staging server (ADR-0007). */
    val xttvHost: String = "https://oettv.xttv.at"
}
