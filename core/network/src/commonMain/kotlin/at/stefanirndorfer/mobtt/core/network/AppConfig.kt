package at.stefanirndorfer.mobtt.core.network

/**
 * [appVersion] is the app's user-visible version; it goes out in the `User-Agent` XTTV sees.
 * [xttvApiKey] comes from untracked local config (`local.properties`, `Secrets.xcconfig`) and is empty when unset.
 * [supportUrl] is the developer's tip page, from the same local config; null while unset, and Settings then hides its
 * "Buy me a coffee" row. Each platform sets it on its own, so an App Store rejection hides the row on iOS alone.
 */
data class AppConfig(
    val environment: Environment,
    val appVersion: String,
    val xttvApiKey: String = "",
    val supportUrl: String? = null,
) {
    override fun toString(): String = "AppConfig(environment=$environment, appVersion=$appVersion, xttvApiKey=<redacted>)"

    /** Every environment that talks to XTTV reads production; XTTV has no staging server (ADR-0007). */
    val xttvHost: String = "https://oettv.xttv.at"

    /** Read only for Rating histories, and only while [ratingHistoryEnabled] (ADR-0010). */
    val ratingsCentralHost: String = "https://www.ratingscentral.com"

    /** Ratings Central's explanation of its Rating and Deviation, linked from the Rating history's info sheet. */
    val ratingsCentralExplanationUrl: String = "$ratingsCentralHost/HowItWorks.php"

    /**
     * On in every environment since Ratings Central agreed to the app reading its history export (ADR-0010). Kept as a
     * switch so the section can be turned off again in one place.
     */
    val ratingHistoryEnabled: Boolean = true

    /**
     * The domain serving Fixture links (ADR-0011), or null until it is registered: Fixtures are then shared without a
     * link or QR code. Set it together with the Android `linksHost` placeholder and the iOS `LINKS_HOST`.
     */
    val linksHost: String? = null

    /** Sharing a Fixture is on where it can be tried, and off for `beta` and `prod` until MobTT is in both stores. */
    val fixtureSharingEnabled: Boolean = environment == Environment.Deb || environment == Environment.Dev
}
