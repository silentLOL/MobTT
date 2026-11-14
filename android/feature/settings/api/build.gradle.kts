plugins {
    id("bat.android.library")
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "at.stefanirndorfer.mobtt.feature.settings.api"
}

dependencies {
    api(project(":android:feature:navigation"))
    api(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}
