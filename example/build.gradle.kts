plugins {
    id("de.charlex.convention.android.application")
    id("de.charlex.convention.kotlin.multiplatform.mobile")
    id("de.charlex.convention.compose.multiplatform")
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.uiTooling)

                implementation(project(":revealswipe"))
            }
        }
    }
}

android {
    namespace = "de.charlex.compose.revealswipe.example"

    defaultConfig {
        applicationId = "de.charlex.compose.revealswipe.example"
        versionCode = 1
        versionName = "1.0"
    }
}