package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.preview

import at.stefanirndorfer.batmate.domain.club.Team
import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.user.PlayerDetail

val playerDetailPreview = PlayerDetail(
    Player("anna", "Anna Gruber", "olympic"),
    "TTC Olympic",
    "WTTV",
    listOf(
        Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga"),
        Team("olympic-3", "TTC Olympic 3", "olympic", "bezirksliga"),
    ),
    rcPoints = 1992,
    ageGroup = "U171",
)

val playerDetailNoTeamsPreview = PlayerDetail(Player("anna", "Anna Gruber", "olympic"), "TTC Olympic", "WTTV")

val playerDetailNoClubPreview = PlayerDetail(Player("anna", "Anna Gruber", "olympic"), null, null)

val playerDetailLongNamesPreview = PlayerDetail(
    Player("pichler", "Katharina Habsburg-Lothringen-Esterházy", "stpoelten"),
    "Union Tischtennisclub St. Pölten Nord-West",
    "Niederösterreichischer Tischtennisverband",
    listOf(Team("stpoelten-1", "Union Tischtennisclub St. Pölten Nord-West 1", "stpoelten", "bundesliga")),
)
