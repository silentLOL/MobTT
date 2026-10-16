plugins {
    id("bat.kmp.library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(libs.androidx.datastore.preferencesCore)
        implementation(libs.koin.core)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.koin.android)
    }
}
