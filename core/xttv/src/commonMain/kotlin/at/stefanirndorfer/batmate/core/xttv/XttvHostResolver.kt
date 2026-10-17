package at.stefanirndorfer.batmate.core.xttv

/**
 * The one place that decides which XTTV host serves an Association (ADR-0008). Associations without an entry
 * in [hosts] are served by [defaultHost].
 */
class XttvHostResolver(val defaultHost: String, private val hosts: Map<String, String> = emptyMap()) {
    fun hostFor(associationId: String): String = hosts[associationId] ?: defaultHost
}
