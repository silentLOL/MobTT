plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.mobtt.feature.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
}
