plugins {
    id("bat.kmp.library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(project(":core:xttv"))
        implementation(project(":domain:club"))
        implementation(project(":domain:player"))
        implementation(libs.koin.core)
    }
    sourceSets.commonTest.dependencies {
        implementation(libs.kotlinx.coroutines.test)
    }
}
