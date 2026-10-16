plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
}
