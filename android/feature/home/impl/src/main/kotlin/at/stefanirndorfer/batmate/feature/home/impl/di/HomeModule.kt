package at.stefanirndorfer.batmate.feature.home.impl.di

import at.stefanirndorfer.batmate.feature.home.impl.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
}
