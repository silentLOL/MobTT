package at.stefanirndorfer.batmate.core.xttv

/**
 * XTTV's XML API v1 (`<host>/xml/v1/<method>.php`). The `apikey` parameter is added by the HTTP client, so it
 * never enters a URL here, the cache keys or the logs.
 */
internal object XttvEndpoints {
    /**
     * XTTV ids are unsigned integers. Anything else (a leftover fake-directory id such as `olympic`) is unknown to
     * XTTV, which answers it with an error comment instead of a document, so it is never asked.
     */
    fun isXttvId(id: String) = id.isNotEmpty() && id.all { it in '0'..'9' }

    private fun method(host: String, name: String, query: String? = null) = "$host/xml/v1/$name.php" + query?.let { "?$it" }.orEmpty()

    fun federations(host: String) = method(host, "getFederations")
    fun clubs(host: String, federationId: String) = method(host, "getClubs", "federationId=$federationId")
    fun club(host: String, clubId: String) = method(host, "getClubDetails", "clubId=$clubId")
    fun team(host: String, teamId: String) = method(host, "getTeamDetails", "teamId=$teamId")
    fun leagues(host: String, federationId: String) = method(host, "getLeagues", "federationId=$federationId")
    fun leagueGames(host: String, leagueId: String) = method(host, "getLeagueGames", "leagueId=$leagueId")
    fun leagueStandings(host: String, leagueId: String) = method(host, "getTeamStandings", "leagueId=$leagueId")
}
