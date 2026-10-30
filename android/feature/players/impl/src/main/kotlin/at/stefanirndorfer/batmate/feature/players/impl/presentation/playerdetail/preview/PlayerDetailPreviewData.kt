package at.stefanirndorfer.batmate.feature.players.impl.presentation.playerdetail.preview

import at.stefanirndorfer.batmate.domain.player.Player
import at.stefanirndorfer.batmate.domain.user.PlayerDetail

val playerDetailPreview = PlayerDetail(Player("anna", "Anna Gruber", "olympic"), "TTC Olympic", "WTTV")

val playerDetailNoClubPreview = PlayerDetail(Player("anna", "Anna Gruber", "olympic"), null, null)

val playerDetailLongNamesPreview = PlayerDetail(
    Player("pichler", "Katharina Habsburg-Lothringen-Esterházy", "stpoelten"),
    "Union Tischtennisclub St. Pölten Nord-West",
    "Niederösterreichischer Tischtennisverband",
)
