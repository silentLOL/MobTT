plugins {
    id("bat.kmp.library")
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(libs.ktor.client.core)
        implementation(libs.ktor.client.logging)
        implementation(libs.kermit)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.koin.core)
    }
    sourceSets.androidMain.dependencies {
        implementation(libs.ktor.client.okhttp)
    }
    sourceSets.iosMain.dependencies {
        implementation(libs.ktor.client.darwin)
    }
    sourceSets.commonTest.dependencies {
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.ktor.client.mock)
    }
}
