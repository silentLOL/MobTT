plugins {
    id("bat.kmp.library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.koin.core)
        implementation(project(":core:platform"))
        implementation(project(":core:preferences"))
        implementation(project(":domain:app"))
        implementation(project(":domain:user"))
    }
}
