plugins {
    id("bat.kmp.library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(project(":core:network"))
        implementation(project(":domain:club"))
        implementation(project(":domain:rating"))
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.datetime)
        implementation(libs.kermit)
        implementation(libs.koin.core)
    }
    sourceSets.commonTest.dependencies {
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.ktor.client.mock)
    }
}
