package at.stefanirndorfer.mobtt.core.network

/**
 * [appVersion] is the app's user-visible version; it goes out in the `User-Agent` XTTV sees.
 * [xttvApiKey] comes from untracked local config (`local.properties`, `Secrets.xcconfig`) and is empty when unset.
 */
data class AppConfig(val environment: Environment, val appVersion: String, val xttvApiKey: String = "") {
    override fun toString(): String = "AppConfig(environment=$environment, appVersion=$appVersion, xttvApiKey=<redacted>)"

    /** Every environment that talks to XTTV reads production; XTTV has no staging server (ADR-0007). */
    val xttvHost: String = "https://oettv.xttv.at"
}
