import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning

plugins {
    `kotlin-dsl`
    signing
    `java-gradle-plugin`
    idea
    id("com.vanniktech.maven.publish") version "0.31.0"
    id("com.gradleup.shadow") version "9.1.0"
}

val pluginsVersion: String by project
val pluginArchiveName: String by project
val pluginGroupId: String by project
val spotlessVersion: String by project
val elytraConventionVersion: String by project
val ideaExtVersion: String by project

version = pluginsVersion

group = pluginGroupId

base.archivesName = pluginArchiveName

// wrapperSpotless { alwaysApplySpotless = true }

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

java {
    toolchain {
        vendor.set(JvmVendorSpec.AZUL)
        languageVersion.set(JavaLanguageVersion.of(17))
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    withSourcesJar()
}

kotlin { explicitApi = Warning }

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "elytra convention"
                url = uri("https://jitpack.io")
            }
        }
        filter { includeGroup("com.github.ElytraServers.elytra-conventions") }
    }
}

val shadowImplementation by
    configurations.registering {
        isCanBeConsumed = false
        isCanBeResolved = true

        listOf(
                configurations.compileClasspath,
                configurations.runtimeClasspath,
                configurations.testCompileClasspath,
                configurations.testRuntimeClasspath,
            )
            .forEach { it.get().extendsFrom(this) }
    }

/// skip the shadowRuntimeElements Variants
afterEvaluate {
    val variants = project.components["java"] as AdhocComponentWithVariants
    variants.withVariantsFromConfiguration(configurations.shadowRuntimeElements.get()) { skip() }
}

/// replace outgoing artifacts
listOf("runtimeElements", "apiElements").forEach { name ->
    configurations[name].outgoing.run {
        artifacts.clear()
        artifact(tasks.shadowJar)
    }
}

tasks.jar {
    enabled = true
    archiveClassifier.set("preshadow")
}

tasks.shadowJar {
    configurations.set(provider { listOf(shadowImplementation.get()) })
    archiveClassifier.set("")
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(localGroovy())
    compileOnly(kotlin("gradle-plugin"))
    api(pluginDep("com.diffplug.gradle.spotless", spotlessVersion))
    api(pluginDep("org.jetbrains.gradle.plugin.idea-ext", ideaExtVersion))
    shadowImpl(pluginDep("com.github.ElytraServers.elytra-conventions", elytraConventionVersion)) { isTransitive = false }
}

// refer from gtnhGradle
fun pluginDep(name: String, version: String): String {
    return "${name}:${name}.gradle.plugin:${version}"
}

@Suppress("unused")
fun DependencyHandler.shadowImpl(dependencyNotation: String): Dependency? =
    add(shadowImplementation.name, dependencyNotation)

fun DependencyHandler.shadowImpl(
    dependencyNotation: String,
    dependencyConfiguration: Action<ExternalModuleDependency>,
): Dependency? = addDependencyTo(this, shadowImplementation.name, dependencyNotation, dependencyConfiguration)

gradlePlugin {
    plugins {
        /// spotless wrapper
        create("spotlessWrapper") {
            id = "$group.spotless-wrapper"
            implementationClass = "${properties["spotlessWrapperPluginClass"]}"
            displayName = "SpotlessWrapper"
            description = "Wrapping my usual spotless plugin settings."
        }

        /// gtnh dependency declaration convention
        create("dependencyDeclarationConvention") {
            id = "$group.dependency-declaration-convention"
            implementationClass = "${properties["dependencyDeclarationConventionClass"]}"
            displayName = "DependencyDeclarationConvention"
            description = "Better dependency declaration"
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Plugin-Archive-Name" to pluginArchiveName,
            "Plugin-Version" to pluginsVersion,
            "Gradle-Version" to gradle.gradleVersion,

            /// dependencies for plugin
            "Plugin-Dependency-Spotless-Version" to spotlessVersion,
            "Plugin-Dependency-Idea-ext-Plugin-Version" to ideaExtVersion,

            /// elytra convention plugin
            "Shadow-Dependency-Elytra-Convention-Version" to elytraConventionVersion,
            "Shadow-Dependency-Elytra-Convention-Licence" to "MIT Licence",
            "Elytra-Convention-GitHub" to "ElytraServers:elytra-conventions",
            "GitHubUrl" to "https://github.com/ElytraServers/elytra-conventions",

            /// behavior
            "Plugin-Capability" to "auto-applies: spotless, idea-ext, elytra-conventions",
        )
    }
}

mavenPublishing {
    configure(GradlePlugin(javadocJar = JavadocJar.None(), sourcesJar = true))
    coordinates(group as String, pluginArchiveName, version as String)

    pom {
        @Suppress("UNCHECKED_CAST")
        (rootProject.extra["configurePom"] as (MavenPom.(String) -> Unit))(this, pluginArchiveName)
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

/// add pre-shadow jar
afterEvaluate {
    publishing.publications.withType(MavenPublication::class.java).all {
        if (name.endsWith("PluginMarkerMaven")) return@all
        artifact(tasks.jar)
    }
}
