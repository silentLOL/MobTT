package at.stefanirndorfer.batmate.core.xttv

/** PROVISIONAL: XTTV documents no URLs yet, so these paths are assumed. Ticket 10 checks them against the live API. */
internal object XttvEndpoints {
    fun federations(host: String) = "$host/api/getFederations"
}
