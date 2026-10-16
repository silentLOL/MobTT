package at.stefanirndorfer.batmate.core.fakedirectory

import at.stefanirndorfer.batmate.domain.club.AssociationRepository
import at.stefanirndorfer.batmate.domain.club.ClubRepository
import at.stefanirndorfer.batmate.domain.player.PlayerRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

/** Non-production only: apps include this module in debug builds, never in release. */
val fakeDirectoryModule = module {
    singleOf(::FakeDirectory) binds arrayOf(
        PlayerRepository::class,
        ClubRepository::class,
        AssociationRepository::class,
    )
}
