package at.stefanirndorfer.mobtt.feature.club.impl.presentation.leaguedetail.preview

import at.stefanirndorfer.mobtt.domain.club.Fixture
import at.stefanirndorfer.mobtt.domain.club.FixtureScore
import at.stefanirndorfer.mobtt.domain.club.FixtureStatus
import at.stefanirndorfer.mobtt.domain.club.League
import at.stefanirndorfer.mobtt.domain.club.LeagueDetail
import at.stefanirndorfer.mobtt.domain.club.Standing
import kotlin.time.Instant

private fun fixture(id: String, home: String, away: String, startsAt: Long, venue: String? = null, score: FixtureScore? = null) = Fixture(
    id = id,
    homeTeamId = home,
    homeTeamName = home,
    awayTeamId = away,
    awayTeamName = away,
    startsAt = Instant.fromEpochSeconds(startsAt),
    venue = venue,
    status = if (score == null) FixtureStatus.Scheduled else FixtureStatus.Finished(score),
)

val leagueDetailPreview = LeagueDetail(
    league = League("landesliga1", "1. Landesliga Herren"),
    standings = listOf(
        Standing(1, "voest-1", "SK VÖEST Wien 1", played = 6, points = 11),
        Standing(2, "olympic-1", "TTC Olympic 1", played = 6, points = 9),
        Standing(3, "nussdorf-1", "Nussdorf TTC 1", played = 6, points = 4),
    ),
    fixtures = listOf(
        fixture("f3", "Nussdorf TTC 1", "TTC Olympic 1", 1_799_000_000, score = FixtureScore(4, 6)),
        fixture("f1", "TTC Olympic 1", "SK VÖEST Wien 1", 1_800_000_000, venue = "Sporthalle Olympic"),
        fixture("f4", "Nussdorf TTC 1", "SK VÖEST Wien 1", 1_800_500_000),
    ),
)

val leagueDetailEmptyPreview = leagueDetailPreview.copy(standings = emptyList(), fixtures = emptyList())

val leagueDetailLongNamesPreview = LeagueDetail(
    league = League("g", "Gebietsliga Nord Herren Oberes Playoff"),
    standings = listOf(
        Standing(1, "h1", "Sportunion Hollabrunn Tischtennis 1", played = 4, points = 8),
        Standing(2, "s1", "Union Tischtennisclub St. Pölten Nord-West 1", played = 4, points = 7),
    ),
    fixtures = listOf(
        fixture(
            "f1",
            "Union Tischtennisclub St. Pölten Nord-West 1",
            "Sportunion Hollabrunn Tischtennis 1",
            1_800_000_000,
            venue = "Mehrzweckhalle der Marktgemeinde St. Pölten Nord-West",
        ),
    ),
)
