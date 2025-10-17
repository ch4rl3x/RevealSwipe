package de.charlex.convention

import com.android.build.gradle.LibraryExtension
import de.charlex.convention.config.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                namespace = "de.charlex.${project.name.replace("-", ".")}"
            }
        }
    }
}