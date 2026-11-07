package at.stefanirndorfer.mobtt.domain.user

/**
 * Where the developer's tip page is, or null when none is set: Settings then shows no "Buy me a coffee" row. Only an
 * `https://` address counts, so a typo or a stray `file:` or `intent:` value in build config never reaches a browser.
 */
class SupportLink(url: String?) {
    val url: String? = url?.trim()?.takeIf { it.startsWith("https://") }
}
