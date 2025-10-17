package de.charlex.convention.config

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import de.charlex.convention.libs

fun KotlinMultiplatformExtension.configureAndroidTarget() {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(project.libs.versions.jvmTarget.get()))
        }
    }
}

fun KotlinMultiplatformExtension.configureIosTargets(baseName: String? = null) {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            this.baseName = baseName ?: project.path.substring(1).replace(':', '-')
                .replace("-", "_") // workaround for https://github.com/luca992/multiplatform-swiftpackage/issues/12
            isStatic = true
        }
    }
}