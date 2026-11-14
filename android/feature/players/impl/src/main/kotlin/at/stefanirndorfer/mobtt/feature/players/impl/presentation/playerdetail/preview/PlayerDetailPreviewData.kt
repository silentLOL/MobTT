package at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.preview

import at.stefanirndorfer.mobtt.core.ui.section.SectionUi
import at.stefanirndorfer.mobtt.domain.club.Team
import at.stefanirndorfer.mobtt.domain.player.Player
import at.stefanirndorfer.mobtt.domain.rating.Rating
import at.stefanirndorfer.mobtt.domain.rating.RatingHistory
import at.stefanirndorfer.mobtt.domain.rating.RatingUpdate
import at.stefanirndorfer.mobtt.domain.user.PlayerClub
import at.stefanirndorfer.mobtt.domain.user.PlayerTeams
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.PlayerDetailUiState
import at.stefanirndorfer.mobtt.feature.players.impl.presentation.playerdetail.models.RatingHistorySectionUi
import kotlinx.datetime.LocalDate

private val anna = Player("anna", "Anna Gruber", "olympic")

private val olympic = SectionUi.Loaded(PlayerClub("TTC Olympic", "WTTV"))

val playerDetailPreview = PlayerDetailUiState.Content(
    player = anna,
    club = olympic,
    teams = SectionUi.Loaded(
        PlayerTeams(
            listOf(
                Team("olympic-1", "TTC Olympic 1", "olympic", "landesliga"),
                Team("olympic-3", "TTC Olympic 3", "olympic", "bezirksliga"),
            ),
            rcPoints = 1992,
            ageGroup = "U171",
        ),
    ),
)

val playerDetailSectionsLoadingPreview = PlayerDetailUiState.Content(anna)

val playerDetailSectionsUnavailablePreview = PlayerDetailUiState.Content(anna, club = SectionUi.Unavailable, teams = SectionUi.Unavailable)

val playerDetailNoTeamsPreview = PlayerDetailUiState.Content(anna, club = olympic, teams = SectionUi.Loaded(PlayerTeams()))

val playerDetailNoClubPreview = PlayerDetailUiState.Content(anna, club = SectionUi.Loaded(null), teams = SectionUi.Loaded(PlayerTeams()))

val playerDetailLongNamesPreview = PlayerDetailUiState.Content(
    player = Player("pichler", "Katharina Habsburg-Lothringen-Esterházy", "stpoelten"),
    club = SectionUi.Loaded(PlayerClub("Union Tischtennisclub St. Pölten Nord-West", "Niederösterreichischer Tischtennisverband")),
    teams = SectionUi.Loaded(
        PlayerTeams(listOf(Team("stpoelten-1", "Union Tischtennisclub St. Pölten Nord-West 1", "stpoelten", "bundesliga"))),
    ),
)

/** Ten weekly Rating updates from Ratings Central's unrated default, as a new Player's first season reads. */
val ratingHistoryPreview = RatingHistory(
    listOf(331 to 162, 381 to 59, 400 to 57, 447 to 65, 519 to 86, 498 to 80, 540 to 77, 562 to 74, 551 to 72, 590 to 70)
        .runningFold(null as RatingUpdate?) { previous, (points, deviation) ->
            val week = previous?.let { it.date.toEpochDays() + 7 } ?: LocalDate(2026, 9, 6).toEpochDays()
            val before = previous?.after ?: Rating(1450, 450)
            RatingUpdate(LocalDate.fromEpochDays(week), "OÖTTV Meisterschaft 2026/27", before, Rating(points, deviation), points - before.points)
        }
        .filterNotNull(),
)

val playerDetailWithRatingHistoryPreview = playerDetailPreview.copy(
    ratingHistory = RatingHistorySectionUi(history = SectionUi.Loaded(ratingHistoryPreview)),
)
