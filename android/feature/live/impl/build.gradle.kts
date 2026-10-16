plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.live.impl"
}

dependencies {
    implementation(project(":android:feature:live:api"))
    implementation(project(":android:core:design-system"))
    implementation(project(":android:core:localization"))
    implementation(libs.androidx.compose.material3)
}
