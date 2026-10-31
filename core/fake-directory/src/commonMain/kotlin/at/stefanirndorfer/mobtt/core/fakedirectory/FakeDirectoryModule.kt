package at.stefanirndorfer.mobtt.core.fakedirectory

import at.stefanirndorfer.mobtt.domain.club.AssociationRepository
import at.stefanirndorfer.mobtt.domain.club.ClubRepository
import at.stefanirndorfer.mobtt.domain.club.FixtureRepository
import at.stefanirndorfer.mobtt.domain.club.LeagueRepository
import at.stefanirndorfer.mobtt.domain.club.TeamRepository
import at.stefanirndorfer.mobtt.domain.player.PlayerRepository
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
}
