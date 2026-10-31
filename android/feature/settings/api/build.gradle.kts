plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.mobtt.feature.settings.api"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
