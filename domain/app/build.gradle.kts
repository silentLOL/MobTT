plugins {
    id("bat.kmp.library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(project(":core:platform"))
        implementation(libs.koin.core)
    }
}
