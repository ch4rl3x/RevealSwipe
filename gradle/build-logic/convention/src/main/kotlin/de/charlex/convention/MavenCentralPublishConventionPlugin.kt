package de.charlex.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MavenCentralPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
                apply("signing")
                apply("org.jetbrains.dokka")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                if (pluginManager.hasPlugin("com.android.library")) {
                    androidTarget {
                        publishLibraryVariants("release")
                    }
                }
            }

            val javadocJar = tasks.register("javadocJar", Jar::class.java) {
                archiveClassifier.set("javadoc")
                from(tasks.getByName("dokkaGeneratePublicationHtml"))
            }

            extensions.configure<PublishingExtension> {
                publications.withType<MavenPublication> {
                    artifact(javadocJar)
                    pom {
                        name.set(project.name)
                        val pom = this
                        project.afterEvaluate {
                            // description seems to be only available after evaluation
                            pom.description.set(project.description)
                        }
                        url.set("https://github.com/ch4rl3x/compose-cache")
                        licenses {
                            license {
                                name.set("Apache-2.0 License")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                            developers {
                                developer {
                                    id.set("ch4rl3x")
                                    name.set("Alexander Karkossa")
                                    email.set("alexander.karkossa@googlemail.com")
                                }
                                developer {
                                    id.set("kalinjul")
                                    name.set("Julian Kalinowski")
                                    email.set("julakali@gmail.com")
                                }
                            }
                            scm {
                                connection.set("scm:git:github.com/ch4rl3x/compose-cache.git")
                                developerConnection.set("scm:git:ssh://github.com/ch4rl3x/compose-cache.git")
                                url.set("https://github.com/ch4rl3x/compose-cache/tree/main")
                            }
                        }
                    }
                }
            }

            extensions.configure<SigningExtension> {
                useInMemoryPgpKeys(
                    getLocalProperty("SIGNING_KEY_ID") ?: System.getenv("SIGNING_KEY_ID"),
                    getLocalProperty("SIGNING_KEY") ?: System.getenv("SIGNING_KEY"),
                    getLocalProperty("SIGNING_KEY_PASSWORD") ?: System.getenv("SIGNING_KEY_PASSWORD"),
                )
                val publishing = extensions.getByType<PublishingExtension>()
                sign(publishing.publications)
            }


            //region Fix Gradle warning about signing tasks using publishing task outputs without explicit dependencies
            // https://github.com/gradle/gradle/issues/26091
            tasks.withType<AbstractPublishToMaven>().configureEach {
                val signingTasks = tasks.withType<Sign>()
                mustRunAfter(signingTasks)
            }
            //endregion
        }
    }
}