plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.settings.api"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
