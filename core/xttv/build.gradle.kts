plugins {
    id("bat.kmp.library")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(project(":core:network"))
        implementation(project(":domain:club"))
        implementation(libs.kotlinx.serialization.core)
        implementation(libs.xmlutil.serialization)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kermit)
    }
    sourceSets.commonTest.dependencies {
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.ktor.client.mock)
    }
}
