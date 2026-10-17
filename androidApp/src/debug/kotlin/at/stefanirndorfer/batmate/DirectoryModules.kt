package at.stefanirndorfer.batmate

import at.stefanirndorfer.batmate.core.fakedirectory.fakeDirectoryModule
import at.stefanirndorfer.batmate.core.network.Environment
import org.koin.core.module.Module

/** Only `deb` binds the fake directory; the other flavors read XTTV through `initKoin`. */
fun directoryModules(environment: Environment): List<Module> {
    if (environment == Environment.Deb) {
        return listOf(fakeDirectoryModule)
    }
    return emptyList()
}
