plugins {
    id("bat.kmp.library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.koin.core)
    }
}
