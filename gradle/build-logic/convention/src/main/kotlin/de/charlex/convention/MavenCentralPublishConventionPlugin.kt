package de.charlex.convention

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
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
import groovy.util.Node
import javax.inject.Inject


class MavenCentralPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create(
            "mavenPublishConfig",
            MavenPublishExtension::class.java
        )

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
            publications.withType<MavenPublication>().configureEach {
                artifact(javadocJar)
                pom {
                    name.set(project.provider { extension.name ?: project.name })
                    description.set(project.provider { extension.description ?: project.description })
                    url.set(project.provider { extension.url ?: "https://github.com/ch4rl3x/${project.name}" })

                    licenses {
                        license {
                            name.set("Apache-2.0 License")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    scm {
                        connection.set(project.provider { extension.scm.connection })
                        developerConnection.set(project.provider { extension.scm.developerConnection })
                        url.set(project.provider { extension.scm.url })
                    }

                    withXml {
                        if (extension.developers.isNotEmpty()) {
                            val root = asNode()
                            // Prüfen ob bereits ein developers-Knoten existiert
                            val existing = root.children().firstOrNull { child ->
                                child is Node && child.name() == "developers"
                            } as Node?
                            val devsNode = existing ?: root.appendNode("developers")
                            extension.developers.forEach { dev ->
                                val devNode = devsNode.appendNode("developer")
                                dev.id?.let { devNode.appendNode("id", it) }
                                dev.name?.let { devNode.appendNode("name", it) }
                                dev.email?.let { devNode.appendNode("email", it) }
                            }
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

abstract class MavenPublishExtension @Inject constructor(objects: ObjectFactory) {
    var name: String?
        get() = nameProperty.orNull
        set(value) = nameProperty.set(value)
    private val nameProperty: Property<String> = objects.property(String::class.java)

    var description: String?
        get() = descriptionProperty.orNull
        set(value) = descriptionProperty.set(value)
    private val descriptionProperty: Property<String> = objects.property(String::class.java)

    var url: String?
        get() = urlProperty.orNull
        set(value) = urlProperty.set(value)
    private val urlProperty: Property<String> = objects.property(String::class.java)

    val developers: MutableList<DeveloperConfig> = mutableListOf()
    fun developers(action: Action<in DeveloperContainer>) {
        val container = DeveloperContainer()
        action.execute(container)
        developers.addAll(container.developers)
    }

    val scm: SCMConfig = objects.newInstance(SCMConfig::class.java)
    fun scm(action: Action<in SCMConfig>) {
        action.execute(scm)
    }

    abstract class SCMConfig @Inject constructor(objects: ObjectFactory) {
        var connection: String?
            get() = connectionProperty.orNull
            set(value) = connectionProperty.set(value)
        private val connectionProperty: Property<String> = objects.property(String::class.java)

        var developerConnection: String?
            get() = developerConnectionProperty.orNull
            set(value) = developerConnectionProperty.set(value)
        private val developerConnectionProperty: Property<String> = objects.property(String::class.java)

        var url: String?
            get() = urlProperty.orNull
            set(value) = urlProperty.set(value)
        private val urlProperty: Property<String> = objects.property(String::class.java)
    }

    class DeveloperContainer {
        internal val developers = mutableListOf<DeveloperConfig>()
        fun developer(action: Action<in DeveloperConfig>) {
            val dev = DeveloperConfig()
            action.execute(dev)
            developers.add(dev)
        }
    }

    class DeveloperConfig {
        var id: String? = null
        var name: String? = null
        var email: String? = null
    }
}