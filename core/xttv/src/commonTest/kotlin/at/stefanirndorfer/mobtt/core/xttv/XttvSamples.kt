package at.stefanirndorfer.mobtt.core.xttv

/** Answers shaped like XTTV's XML API v1 (trimmed from recorded responses, with unknown extra elements kept). */
object XttvSamples {
    const val HOST = "https://xttv.example.test"
    private const val BASE = "$HOST/xml/v1"

    const val FEDERATIONS_URL = "$BASE/getFederations.php"
    const val FEDERATIONS = """
        <federation_list>
            <season_id>26</season_id>
            <federation><id>188</id><name>Niederösterreichischer Tischtennisverband</name><short_name>Niederösterreich</short_name><code>NÖTTV</code><www>x</www></federation>
            <federation><id>190</id><name>Kärntner Tischtennisverband</name><short_name>Kärnten</short_name><code>KTTV</code><www></www></federation>
        </federation_list>
    """
    const val NO_FEDERATIONS = "<federation_list><season_id>26</season_id></federation_list>"

    const val CLUBS_188_URL = "$BASE/getClubs.php?federationId=188"
    const val CLUBS_188 = """
        <club_list>
            <federation_id>188</federation_id>
            <club><id>1</id><name>ATUS Gumpoldskirchen</name><short_name>Gumpoldskirchen</short_name><code>AGUM</code></club>
            <club><id>3</id><name>Union Raika Allhartsberg</name><short_name>Allhartsberg</short_name><code>ALLH</code></club>
        </club_list>
    """
    const val CLUBS_190_URL = "$BASE/getClubs.php?federationId=190"
    const val CLUBS_190 = """
        <club_list>
            <federation_id>190</federation_id>
            <club><id>77</id><name>TTC Krumpendorf</name><short_name>Krumpendorf</short_name><code>KRUM</code></club>
        </club_list>
    """
    const val NO_CLUBS = "<club_list><federation_id>190</federation_id></club_list>"

    const val CLUB_140_URL = "$BASE/getClubDetails.php?clubId=140"
    const val CLUB_140 = """
        <club>
            <id>140</id><name>LINZ AG-FROSCHBERG</name><short_name>Linz-Froschberg AG</short_name><code>FROB</code>
            <federation_id>191</federation_id>
            <address><street>Europaplatz 31</street><city>Linz</city></address>
            <team_list>
                <team><id>501</id><name>LINZ AG Froschberg 1</name><code>FROB1</code><league_id>70</league_id><league_name>Bundesliga</league_name></team>
                <team><id>502</id><name>Linz-Froschberg AG 2</name><code>FROB2</code><league_id>71</league_id><league_name>Landesliga</league_name></team>
                <team><id>501</id><name>LINZ AG Froschberg 1</name><code>FROB1</code><league_id>70</league_id><league_name>Bundesliga</league_name></team>
            </team_list>
        </club>
    """
    const val CLUB_141_URL = "$BASE/getClubDetails.php?clubId=141"
    const val CLUB_141_WITHOUT_TEAMS = "<club><id>141</id><name>ASKÖ Leer</name><federation_id>191</federation_id></club>"

    const val TEAM_501_URL = "$BASE/getTeamDetails.php?teamId=501"
    const val TEAM_501 = """
        <team_details>
            <id>501</id><name>LINZ AG Froschberg 1</name><code>FROB1</code><club_id>140</club_id><club_name>Linz-Froschberg AG</club_name>
            <league_id>70</league_id><league_name>Bundesliga</league_name>
            <team_location><id>165</id><name>Sportpark Lissfeld</name></team_location>
            <player><id>60473</id><first_name>Phatsaraphon</first_name><last_name>Wonglakhon</last_name><rc_type>rc</rc_type><rc_points>1992</rc_points><rc_deviation>53</rc_deviation><age_group>U171</age_group></player>
        </team_details>
    """
    const val UNKNOWN_TEAM_URL = "$BASE/getTeamDetails.php?teamId=999"
    const val UNKNOWN_TEAM = "<team_details><id>999</id><team_leader/></team_details>"
    const val UNKNOWN_CLUB_URL = "$BASE/getClubDetails.php?clubId=999"
    const val UNKNOWN_CLUB = "<club/>"
    const val INVALID_API_KEY = "<!--Invalid API key.--><xml></xml>"
    const val TEAM_503_URL = "$BASE/getTeamDetails.php?teamId=503"
    const val TEAM_503_WITHOUT_LEAGUE =
        "<team_details><id>503</id><name>FROB 3</name><club_id>140</club_id><league_id></league_id></team_details>"

    const val LEAGUES_188_URL = "$BASE/getLeagues.php?federationId=188"
    const val LEAGUES_188 = """
        <federation_league_list>
            <federation_id>188</federation_id><season_id>26</season_id>
            <league><id>8001</id><name>NÖ Landesliga</name></league>
        </federation_league_list>
    """
    const val LEAGUES_190_URL = "$BASE/getLeagues.php?federationId=190"
    const val LEAGUES_190 = """
        <federation_league_list>
            <federation_id>190</federation_id><season_id>26</season_id>
            <league><id>70</id><name>Kärntner Liga</name></league>
        </federation_league_list>
    """

    const val GAMES_70_URL = "$BASE/getLeagueGames.php?leagueId=70"
    val GAMES_70 = """
        <games_list>
            <league_id>70</league_id><team_id></team_id>
            ${game("9001", "501", "TTC Olympic 1", "600", "SK VÖEST 1", "01.12.2026", "18:00", location = "Sporthalle Olympic")}
            ${game("9002", "600", "SK VÖEST 1", "501", "TTC Olympic 1", "01.07.2026", "18:00", home = "4", away = "6")}
            ${game("9003", "600", "SK VÖEST 1", "700", "Nussdorf 1", "02.12.2026", "18:00")}
            ${game("9007", "600", "SK VÖEST 1", "700", "Nussdorf 1", "03.12.2026", "18:00", home = "0", away = "0")}
            ${game("9004", "501", "TTC Olympic 1", "700", "Nussdorf 1", "soon", "")}
            ${game("9005", "501", "TTC Olympic 1", "700", "Nussdorf 1", "25.10.2026", "01:30", location = " ")}
            ${game("9006", "501", "TTC Olympic 1", "700", "Nussdorf 1", "25.10.2026", "03:30")}
        </games_list>
    """

    const val STANDINGS_70_URL = "$BASE/getTeamStandings.php?leagueId=70"
    val STANDINGS_70 = """
        <team_standing>
            <league_id>70</league_id><tab_show>1</tab_show>
            ${row(2, "600", "SK VÖEST 1", played = 5, points = 8)}
            ${row(1, "501", "TTC Olympic 1", played = 5, points = 10)}
        </team_standing>
    """
    const val STANDINGS_99_URL = "$BASE/getTeamStandings.php?leagueId=99"
    const val NO_STANDINGS = "<team_standing><league_id>99</league_id></team_standing>"

    private fun game(
        id: String,
        homeId: String,
        homeName: String,
        awayId: String,
        awayName: String,
        date: String,
        time: String,
        home: String = "",
        away: String = "",
        location: String = "",
    ) = """
        <game>
            <game_id>$id</game_id><played>${home.length.coerceAtMost(1)}</played><date>$date</date><time>$time</time>
            <team_home_id>$homeId</team_home_id><team_home_short_name>$homeName</team_home_short_name>
            <team_away_id>$awayId</team_away_id><team_away_short_name>$awayName</team_away_short_name>
            <matches_won_home>$home</matches_won_home><matches_won_away>$away</matches_won_away>
            <location>$location</location><address><city>Wien</city></address>
        </game>
    """

    private fun row(rank: Int, teamId: String, name: String, played: Int, points: Int) = """
        <standing_row>
            <rank>$rank</rank><rank_effect></rank_effect><team_id>$teamId</team_id><team_short_name>$name</team_short_name>
            <games_played>$played</games_played><games_won>1</games_won><points>$points</points><sets_won>9</sets_won>
        </standing_row>
    """

    /**
     * `getSingleStandings` as recorded 2026-10-31 for a 2. Landesliga (trimmed to three rows). Every field a Player row
     * carries is here, including the ranking ones the Player screen does not read yet (research ticket 01 of
     * player-detail-teams). `ranked` is 0 for a Player below the League's minimum games.
     */
    const val SINGLE_STANDINGS_RECORDED = """
        <single_standing>
         <league_id>8937</league_id>
         <rc_usage>1</rc_usage>
         <rc_type>rc</rc_type>
         <standing_row>
          <ranked>1</ranked><rank>1</rank><player_id>3680</player_id><player_first_name>Reinhard</player_first_name>
          <player_last_name>Blauensteiner</player_last_name><player_name_suffix></player_name_suffix>
          <player_pass_number>3386</player_pass_number><team_id>2271</team_id><team_code>UPÖL1</team_code><club_id>99</club_id>
          <games_played>21</games_played><matches_won>52</matches_won><matches_lost>2</matches_lost>
          <player_rc_id>27808</player_rc_id><player_rc_points>1757</player_rc_points><player_rc_deviation>64</player_rc_deviation>
         </standing_row>
         <standing_row>
          <ranked>0</ranked><rank>5</rank><player_id>62323</player_id><player_first_name>Beispiel</player_first_name>
          <player_last_name>Spieler</player_last_name><player_name_suffix></player_name_suffix>
          <player_pass_number>9542</player_pass_number><team_id>54094</team_id><team_code>WOLK1</team_code><club_id>112</club_id>
          <games_played>1</games_played><matches_won>0</matches_won><matches_lost>2</matches_lost>
          <player_rc_id>28424</player_rc_id><player_rc_points>1576</player_rc_points><player_rc_deviation>68</player_rc_deviation>
         </standing_row>
        </single_standing>
    """
}
