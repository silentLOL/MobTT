package at.stefanirndorfer.mobtt.core.xttv

private const val BASE = "${XttvSamples.HOST}/xml/v1"

/** Game details recorded from XTTV on 2026-11-02 (minus the XML declaration), plus a hand-written Game that is not yet played. */
object XttvGameDetailsSamples {
    const val PLAYED_464821_URL = "$BASE/getGameDetails.php?gameId=464821"

    /** `getGameDetails` for a played Fixture: ten Matches, two of them doubles. */
    val PLAYED_464821 = """
<game>
 <id>464821</id>
 <period_nr>1</period_nr>
 <round_nr>1</round_nr>
 <time>19:30</time>
 <played>1</played>
 <confirmed>1</confirmed>
 <date>02.10.2026</date>
 <time>19:30</time>
 <location>Sporthalle Hagenberg</location>
 <address>
  <street>Softwarepark 101</street>
  <postal_code>4232</postal_code>
  <city>Hagenberg </city>
  <latitude>48.37231</latitude>
  <longitude>14.5171</longitude>
 </address>
 <team_home_id>54402</team_home_id>
 <team_home_code>HABE3</team_home_code>
 <team_home_short_name>Hagenberg 3</team_home_short_name>
 <team_away_id>54446</team_away_id>
 <team_away_code>KEFR4</team_away_code>
 <team_away_short_name>Kefermarkt 4</team_away_short_name>
 <home_orientation>S</home_orientation>
 <matches_won_home>10</matches_won_home>
 <matches_won_away>0</matches_won_away>
 <muba_code></muba_code>
 <single_order>1:D2:1;2:A3:2;3:C4:3;4:B1:4;6:A2:6;7:D3:7;8:C1:8;9:B4:9;11:A1:11;12:B2:12;13:C3:13;14:D4:14</single_order>
 <double_order>5:1:5;10:2:10</double_order>
 <player>
  <id>62333</id>
  <position>A</position>
  <first_name>Alexander</first_name>
  <last_name>Frent</last_name>
 </player>
 <player>
  <id>63800</id>
  <position>B</position>
  <first_name>Jakob</first_name>
  <last_name>Irndorfer</last_name>
 </player>
 <player>
  <id>64103</id>
  <position>C</position>
  <first_name>Stefan</first_name>
  <last_name>Irndorfer</last_name>
 </player>
 <player>
  <id>65369</id>
  <position>D</position>
  <first_name>Anton</first_name>
  <last_name>Irndorfer</last_name>
 </player>
 <player>
  <id>64103</id>
  <position>E</position>
  <first_name>Stefan</first_name>
  <last_name>Irndorfer</last_name>
 </player>
 <player>
  <id>65369</id>
  <position>F</position>
  <first_name>Anton</first_name>
  <last_name>Irndorfer</last_name>
 </player>
 <player>
  <id>62333</id>
  <position>G</position>
  <first_name>Alexander</first_name>
  <last_name>Frent</last_name>
 </player>
 <player>
  <id>63800</id>
  <position>H</position>
  <first_name>Jakob</first_name>
  <last_name>Irndorfer</last_name>
 </player>
 <player>
  <id>36931</id>
  <position>1</position>
  <first_name>Andreas</first_name>
  <last_name>Hinterdorfer</last_name>
 </player>
 <player>
  <id>61400</id>
  <position>2</position>
  <first_name>Markus</first_name>
  <last_name>Luger</last_name>
 </player>
 <player>
  <id>62386</id>
  <position>3</position>
  <first_name>Jonathan</first_name>
  <last_name>Himmelbauer</last_name>
 </player>
 <player>
  <id>55179</id>
  <position>4</position>
  <first_name>Luca</first_name>
  <last_name>Holzer</last_name>
 </player>
 <player>
  <id>61400</id>
  <position>5</position>
  <first_name>Markus</first_name>
  <last_name>Luger</last_name>
 </player>
 <player>
  <id>62386</id>
  <position>6</position>
  <first_name>Jonathan</first_name>
  <last_name>Himmelbauer</last_name>
 </player>
 <player>
  <id>36931</id>
  <position>7</position>
  <first_name>Andreas</first_name>
  <last_name>Hinterdorfer</last_name>
 </player>
 <player>
  <id>55179</id>
  <position>8</position>
  <first_name>Luca</first_name>
  <last_name>Holzer</last_name>
 </player>
 <game_result>
  <number>1</number>
  <result_type>G</result_type>
  <result>3:0</result>
  <points></points>
  <type>single</type>
  <player_home>2</player_home>
  <player_away>D</player_away>
 </game_result>
 <game_result>
  <number>2</number>
  <result_type>G</result_type>
  <result>3:0</result>
  <points></points>
  <type>single</type>
  <player_home>3</player_home>
  <player_away>A</player_away>
 </game_result>
 <game_result>
  <number>3</number>
  <result_type>G</result_type>
  <result>3:0</result>
  <points></points>
  <type>single</type>
  <player_home>4</player_home>
  <player_away>C</player_away>
 </game_result>
 <game_result>
  <number>4</number>
  <result_type>G</result_type>
  <result>3:1</result>
  <points></points>
  <type>single</type>
  <player_home>1</player_home>
  <player_away>B</player_away>
 </game_result>
 <game_result>
  <number>5</number>
  <result_type>G</result_type>
  <result>3:0</result>
  <points></points>
  <type>double</type>
  <player_home>5</player_home>
  <player_home>6</player_home>
  <player_away>E</player_away>
  <player_away>F</player_away>
 </game_result>
 <game_result>
  <number>6</number>
  <result_type>G</result_type>
  <result>3:0</result>
  <points></points>
  <type>single</type>
  <player_home>2</player_home>
  <player_away>A</player_away>
 </game_result>
 <game_result>
  <number>7</number>
  <result_type>G</result_type>
  <result>3:0</result>
  <points></points>
  <type>single</type>
  <player_home>3</player_home>
  <player_away>D</player_away>
 </game_result>
 <game_result>
  <number>8</number>
  <result_type>G</result_type>
  <result>3:0</result>
  <points></points>
  <type>single</type>
  <player_home>1</player_home>
  <player_away>C</player_away>
 </game_result>
 <game_result>
  <number>9</number>
  <result_type>G</result_type>
  <result>3:2</result>
  <points></points>
  <type>single</type>
  <player_home>4</player_home>
  <player_away>B</player_away>
 </game_result>
 <game_result>
  <number>10</number>
  <result_type>G</result_type>
  <result>3:1</result>
  <points></points>
  <type>double</type>
  <player_home>7</player_home>
  <player_home>8</player_home>
  <player_away>G</player_away>
  <player_away>H</player_away>
 </game_result>
</game>
"""

    const val SCHEDULED_9100_URL = "$BASE/getGameDetails.php?gameId=9100"

    /** A Fixture in the future: no Matches won, no lineup, no results, and an address with only a city. */
    const val SCHEDULED_9100 = """
        <game>
            <id>9100</id><period_nr>2</period_nr><round_nr>3</round_nr><played>0</played><confirmed>0</confirmed>
            <date>06.03.2027</date><time>18:30</time><time>18:30</time>
            <location>Sporthalle Olympic</location>
            <address><street></street><postal_code></postal_code><city>Wien</city></address>
            <team_home_id>501</team_home_id><team_home_code>OLY1</team_home_code><team_home_short_name>TTC Olympic 1</team_home_short_name>
            <team_away_id>600</team_away_id><team_away_code>VOE1</team_away_code><team_away_short_name>SK VÖEST 1</team_away_short_name>
            <matches_won_home></matches_won_home><matches_won_away></matches_won_away>
        </game>
    """

    const val UNKNOWN_GAME_URL = "$BASE/getGameDetails.php?gameId=1"
    const val UNKNOWN_GAME = "<game><id>1</id></game>"
}
