package at.stefanirndorfer.mobtt.feature.club.impl.presentation.fixturedetail.preview

import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureDetail
import at.stefanirndorfer.mobtt.domain.club.FixtureScore
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.Match
import at.stefanirndorfer.mobtt.domain.club.MatchKind
import at.stefanirndorfer.mobtt.domain.club.MatchScore
import at.stefanirndorfer.mobtt.domain.club.VenueAddress
import kotlin.time.Instant

private val scheduledFixture = Fixture(
    id = "f1",
    homeTeamId = "olympic-1",
    homeTeamName = "TTC Olympic 1",
    awayTeamId = "voest-1",
    awayTeamName = "SK VÖEST Wien 1",
    startsAt = Instant.fromEpochSeconds(1_800_000_000),
    venue = "Sporthalle Olympic",
    status = FixtureStatus.Scheduled,
)

private fun match(number: Int, home: List<String>, away: List<String>, sets: Pair<Int, Int>?) = Match(
    number = number,
    kind = if (home.size > 1) MatchKind.Doubles else MatchKind.Singles,
    homePlayers = home,
    awayPlayers = away,
    score = sets?.let { MatchScore(it.first, it.second) },
)

val fixtureDetailPlayedPreview = FixtureDetail(
    fixture = scheduledFixture.copy(status = FixtureStatus.Finished(FixtureScore(6, 4))),
    address = VenueAddress("Hauptstraße 12", "1010", "Wien"),
    matches = listOf(
        match(1, listOf("Michael Huber"), listOf("Lukas Bauer"), 3 to 1),
        match(2, listOf("Anna Gruber"), listOf("Sophie Wagner"), 1 to 3),
        match(3, listOf("Michael Huber", "Anna Gruber"), listOf("Lukas Bauer", "Sophie Wagner"), 3 to 2),
        match(4, listOf("Anna Gruber"), listOf("Lukas Bauer"), 3 to 0),
    ),
)

val fixtureDetailScheduledPreview = FixtureDetail(
    fixture = scheduledFixture,
    address = VenueAddress(street = null, postalCode = null, city = "Wien"),
    matches = emptyList(),
)

val fixtureDetailLongNamesPreview = FixtureDetail(
    fixture = scheduledFixture.copy(
        homeTeamName = "Union Tischtennisclub St. Pölten Nord-West 1",
        awayTeamName = "Sportunion Hollabrunn Tischtennis 1",
        venue = "Mehrzweckhalle der Marktgemeinde St. Pölten Nord-West",
        status = FixtureStatus.Finished(FixtureScore(2, 8)),
    ),
    address = VenueAddress("Dr.-Karl-Renner-Promenade 123a/Stiege 4", "3100", "St. Pölten"),
    matches = listOf(
        match(
            1,
            listOf("Katharina Habsburg-Lothringen-Esterházy", "Maximilian Mustermann-Musterfrau"),
            listOf("Alexander von Hohenberg-Wartenstein", "Christina Schwarzenberg-Lobkowicz"),
            2 to 3,
        ),
    ),
)

val fixtureDetailNoMatchesPreview = fixtureDetailPlayedPreview.copy(matches = emptyList())
