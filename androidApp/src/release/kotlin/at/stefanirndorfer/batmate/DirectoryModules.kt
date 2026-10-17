package at.stefanirndorfer.batmate

import org.koin.core.module.Module

// Release builds carry no fake directory; the repositories XTTV does not serve yet stay unbound.
val directoryModules: List<Module> = emptyList()
