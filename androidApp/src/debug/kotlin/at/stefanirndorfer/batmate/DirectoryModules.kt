package at.stefanirndorfer.batmate

import at.stefanirndorfer.batmate.core.fakedirectory.fakeDirectoryModule
import org.koin.core.module.Module

// TODO: shrink as tickets 03-06 and 09 bring XTTV-backed repositories; `initKoin` binds those after these and wins.
val directoryModules: List<Module> = listOf(fakeDirectoryModule)
