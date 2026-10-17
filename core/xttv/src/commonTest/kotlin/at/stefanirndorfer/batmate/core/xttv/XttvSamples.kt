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

    const val CLUBS = """
        <clubs>
            <club><id>8846</id><name>TTC Olympic Wien</name><federationId>1</federationId><city>Wien</city></club>
            <club><id>9001</id><name>UTTC Olympia Krems</name><federationId>2</federationId></club>
        </clubs>
    """
    const val NO_CLUBS = "<clubs/>"
    const val CLUB_8846 =
        "<club><id>8846</id><name>TTC Olympic Wien</name><federationId>1</federationId></club>"
    const val CLUB_9001 =
        "<club><id>9001</id><name>UTTC Olympia Krems</name><federationId>2</federationId></club>"

    const val CLUB_8846_TEAMS = """
        <teams>
            <team><id>501</id><name>TTC Olympic Wien 1</name><clubId>8846</clubId><leagueId>70</leagueId></team>
            <team><id>502</id><name>TTC Olympic Wien 2</name><clubId>8846</clubId><leagueId>71</leagueId><captain>x</captain></team>
        </teams>
    """
    const val NO_TEAMS = "<teams/>"
    const val TEAM_501 =
        "<teams><team><id>501</id><name>TTC Olympic Wien 1</name><clubId>8846</clubId><leagueId>70</leagueId></team></teams>"
}
