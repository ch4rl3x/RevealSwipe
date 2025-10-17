import de.charlex.convention.config.configureIosTargets

plugins {
    id("de.charlex.convention.android.library")
    id("de.charlex.convention.kotlin.multiplatform.mobile")
    id("de.charlex.convention.centralPublish")
    id("de.charlex.convention.compose.multiplatform")
}

mavenPublishConfig {
    name = "RevealSwipe"
    description = "A Jetpack Compose library that lets you add swipe-to-reveal behavior in both directions, exposing hidden content (icons, actions) behind your UI, with support for Material 3 and custom reveal directions."
    url = "https://github.com/ch4rl3x/RevealSwipe"

    scm {
        connection = "scm:git:github.com/ch4rl3x/RevealSwipe.git"
        developerConnection = "scm:git:ssh://github.com/ch4rl3x/RevealSwipe.git"
        url = "https://github.com/ch4rl3x/RevealSwipe/tree/main"
    }

    developers {
        developer {
            id = "ch4rl3x"
            name = "Alexander Karkossa"
            email = "alexander.karkossa@googlemail.com"
        }
        developer {
            id = "kalinjul"
            name = "Julian Kalinowski"
            email = "julakali@gmail.com"
        }
    }
}

kotlin {
    configureIosTargets()
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }
    }
}