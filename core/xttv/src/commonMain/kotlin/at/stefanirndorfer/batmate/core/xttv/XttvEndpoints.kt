package at.stefanirndorfer.batmate.core.xttv

/** PROVISIONAL: XTTV documents no URLs yet, so these paths are assumed. Ticket 10 checks them against the live API. */
internal object XttvEndpoints {
    fun federations(host: String) = "$host/api/getFederations"
    fun clubs(host: String) = "$host/api/getClubs"
    fun club(host: String, id: String) = "$host/api/getClubDetails?id=$id"
    fun clubTeams(host: String, clubId: String) = "$host/api/getLeagueTeams?clubId=$clubId"
    fun team(host: String, id: String) = "$host/api/getTeamDetails?id=$id"
    fun leagueGames(host: String, leagueId: String) = "$host/api/getLeagueGames?leagueId=$leagueId"
    fun league(host: String, id: String) = "$host/api/getLeagueDetails?id=$id"
    fun leagueStandings(host: String, leagueId: String) = "$host/api/getTeamStandings?leagueId=$leagueId"
}
