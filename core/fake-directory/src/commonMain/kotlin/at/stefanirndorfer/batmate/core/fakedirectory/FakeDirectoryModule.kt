package at.stefanirndorfer.batmate.core.fakedirectory

import at.stefanirndorfer.batmate.core.xttv.LineupSource
import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.club.FixtureRepository
import at.stefanirndorfer.batmate.domain.club.LeagueRepository
import at.stefanirndorfer.batmate.domain.club.TeamRepository
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import org.koin.dsl.binds
import org.koin.dsl.module

/** Non-production only: apps include this module in debug builds, never in release. */
val fakeDirectoryModule = module {
    single { FakeDirectory() } binds arrayOf(
        PlayerRepository::class,
        ClubRepository::class,
        AssociationRepository::class,
        TeamRepository::class,
        LeagueRepository::class,
        FixtureRepository::class,
    )

    // TODO: remove when XTTV serves lineups (ticket 09); XTTV-backed Teams take their lineups from here until then.
    single<LineupSource> { LineupSource { get<FakeDirectory>().getLineup(it) } }
}
