import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "de.charlex.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17 // hardcode to default android studio embedded jdk version JavaVersion.toVersion(libs.versions.jvmTarget.get())
    targetCompatibility = JavaVersion.VERSION_17 // hardcode to default android studio embedded jdk version  JavaVersion.toVersion(libs.versions.jvmTarget.get())
}
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
    }
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.nexusPublish.gradlePlugin)
    compileOnly(libs.dokka.gradlePlugin)

    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "de.charlex.convention.android.library"
            implementationClass = "de.charlex.convention.AndroidLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "de.charlex.convention.android.application"
            implementationClass = "de.charlex.convention.AndroidApplicationConventionPlugin"
        }
        register("kotlinMultiplatform") {
            id = "de.charlex.convention.kotlin.multiplatform"
            implementationClass = "de.charlex.convention.KotlinMultiplatformConventionPlugin"
        }
        register("kotlinMultiplatformMobile") {
            id = "de.charlex.convention.kotlin.multiplatform.mobile"
            implementationClass = "de.charlex.convention.KotlinMultiplatformMobileConventionPlugin"
        }

        register("composeMultiplatform") {
            id = "de.charlex.convention.compose.multiplatform"
            implementationClass = "de.charlex.convention.ComposeMultiplatformConventionPlugin"
        }
        register("centralPublish") {
            id = "de.charlex.convention.centralPublish"
            implementationClass = "de.charlex.convention.MavenCentralPublishConventionPlugin"
        }
    }
}