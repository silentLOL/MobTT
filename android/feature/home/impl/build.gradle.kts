plugins {
    id("bat.android.library")
}

android {
    namespace = "at.stefanirndorfer.batmate.feature.home.impl"
}

dependencies {
    implementation(project(":android:feature:home:api"))
    implementation(project(":android:core:design-system"))
    implementation(project(":android:core:localization"))
    implementation(libs.androidx.compose.material3)
}
