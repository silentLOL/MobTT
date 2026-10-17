package at.stefanirndorfer.batmate.core.network

import org.koin.core.module.Module
import org.koin.dsl.module

fun networkModule(config: AppConfig): Module = module {
    single { config }
}
