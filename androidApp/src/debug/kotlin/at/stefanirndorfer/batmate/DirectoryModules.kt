package at.stefanirndorfer.batmate

import at.stefanirndorfer.batmate.core.fakedirectory.fakeDirectoryModule
import org.koin.core.module.Module

val directoryModules: List<Module> = listOf(fakeDirectoryModule)
