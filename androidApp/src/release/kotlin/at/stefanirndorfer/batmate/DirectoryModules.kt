package at.stefanirndorfer.batmate

import at.stefanirndorfer.batmate.core.network.Environment
import org.koin.core.module.Module

/** Release builds carry no fake directory; every repository comes from XTTV through `initKoin`. */
@Suppress("UNUSED_PARAMETER")
fun directoryModules(environment: Environment): List<Module> = emptyList()
