package at.stefanirndorfer.batmate.core.xttv

/**
 * ASSUMED XTTV answers, written by hand: XTTV documents no schemas yet. Ticket 10 replaces these with recorded
 * responses; until then they pin down what our mappers expect, not what XTTV sends.
 */
object XttvSamples {
    const val FEDERATIONS = """
        <federations>
            <federation><id>1</id><name>Wiener Tischtennisverband (WTTV)</name><region>Wien</region></federation>
            <federation><id>2</id><name>Niederösterreichischer Tischtennisverband (NÖTTV)</name></federation>
        </federations>
    """
    const val NO_FEDERATIONS = "<federations/>"
}
