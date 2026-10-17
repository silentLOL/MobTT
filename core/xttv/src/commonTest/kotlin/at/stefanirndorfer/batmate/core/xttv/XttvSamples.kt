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

    const val LEAGUE_70_GAMES = """
        <games>
            <game>
                <id>9001</id><homeTeamId>501</homeTeamId><homeTeamName>TTC Olympic Wien 1</homeTeamName>
                <awayTeamId>600</awayTeamId><awayTeamName>SK VÖEST Wien 1</awayTeamName>
                <date>2026-12-01T18:00</date><venue>Sporthalle Olympic</venue><homeMatches></homeMatches>
            </game>
            <game>
                <id>9002</id><homeTeamId>600</homeTeamId><homeTeamName>SK VÖEST Wien 1</homeTeamName>
                <awayTeamId>501</awayTeamId><awayTeamName>TTC Olympic Wien 1</awayTeamName>
                <date>2026-07-01T18:00</date><homeMatches>4</homeMatches><awayMatches>6</awayMatches><extra>x</extra>
            </game>
            <game>
                <id>9003</id><homeTeamId>600</homeTeamId><homeTeamName>SK VÖEST Wien 1</homeTeamName>
                <awayTeamId>700</awayTeamId><awayTeamName>Nussdorf TTC 1</awayTeamName>
                <date>2026-12-02T18:00</date>
            </game>
            <game>
                <id>9004</id><homeTeamId>501</homeTeamId><homeTeamName>TTC Olympic Wien 1</homeTeamName>
                <awayTeamId>700</awayTeamId><awayTeamName>Nussdorf TTC 1</awayTeamName>
                <date>soon</date>
            </game>
            <game>
                <id>9005</id><homeTeamId>501</homeTeamId><homeTeamName>TTC Olympic Wien 1</homeTeamName>
                <awayTeamId>700</awayTeamId><awayTeamName>Nussdorf TTC 1</awayTeamName>
                <date>2026-10-25T01:30</date><venue> </venue>
            </game>
            <game>
                <id>9006</id><homeTeamId>501</homeTeamId><homeTeamName>TTC Olympic Wien 1</homeTeamName>
                <awayTeamId>700</awayTeamId><awayTeamName>Nussdorf TTC 1</awayTeamName>
                <date>2026-10-25T03:30</date>
            </game>
        </games>
    """

    const val LEAGUE_70 = "<leagues><league><id>70</id><name>1. Landesliga Herren</name></league></leagues>"
    const val NO_LEAGUES = "<leagues/>"
    const val LEAGUE_70_STANDINGS = """
        <standings>
            <standing><position>2</position><teamId>600</teamId><teamName>SK VÖEST Wien 1</teamName><played>5</played><points>8</points></standing>
            <standing><position>1</position><teamId>501</teamId><teamName>TTC Olympic Wien 1</teamName><played>5</played><points>10</points><sets>x</sets></standing>
        </standings>
    """
    const val NO_STANDINGS = "<standings/>"
}
