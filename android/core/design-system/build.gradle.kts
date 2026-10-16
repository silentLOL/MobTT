plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3)
}
