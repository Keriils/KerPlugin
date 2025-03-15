import com.diffplug.gradle.spotless.BaseKotlinExtension

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    id("com.diffplug.spotless") version "7.0.2"
    id("com.palantir.git-version") version "3.0.0"
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "com.keriils"

version = gitVersion()

kotlin { jvmToolchain(21) }

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies { implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2") }

java {
    withSourcesJar()
    withJavadocJar()
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

gradlePlugin {
    plugins {
        isAutomatedPublishing = false
        create("spotlessWrapper") {
            id = "$group.spotless-wrapper"
            implementationClass = "$group.SpotlessWrapperPlugin"
            displayName = "SpotlessWrapper"
            description = "Wrapping my usual spotless plugin settings."
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("KerPlugin") {
            artifactId = rootProject.name
            groupId = project.group as String
            version = project.version as String
            from(components["java"])

            pom {
                name.set("KerPlugin")
                description.set("My custom gradle plugin for various generic projects, w....")
                url.set("https://github.com/Keriils/KerPlugin")

                licenses { license { name.set("MIT License") } }

                developers { developer { id.set("Keriils") } }

                scm {
                    connection.set("scm:git:git://github.com/Keriils/KerPlugin.git")
                    developerConnection.set("scm:git:ssh://github.com/Keriils/KerPlugin.git")
                    url.set("https://github.com/Keriils/KerPlugin")
                }
            }
        }

        // From org.gradle.plugin.devel.plugins.MavenPluginPublishPlugin.createMavenMarkerPublication
        gradlePlugin.plugins.forEach { declaration ->
            create<MavenPublication>(declaration.name + "PluginMarkerMaven") {
                artifactId = declaration.id + ".gradle.plugin"
                groupId = declaration.id
                pom {
                    name.set(declaration.displayName)
                    description.set(declaration.description)
                    withXml {
                        val root = asElement()
                        val document = root.ownerDocument
                        val dependencies = root.appendChild(document.createElement("dependencies"))
                        val dependency = dependencies.appendChild(document.createElement("dependency"))
                        val groupId = dependency.appendChild(document.createElement("groupId"))
                        groupId.textContent = project.group.toString()
                        val artifactId = dependency.appendChild(document.createElement("artifactId"))
                        artifactId.textContent = "ker-plugin"
                        val version = dependency.appendChild(document.createElement("version"))
                        version.textContent = project.version.toString()
                    }
                }
            }
        }
    }

    repositories { mavenLocal() }
}

spotless {
    encoding("UTF-8")

    @Suppress("SpellCheckingInspection")
    fun BaseKotlinExtension.applyCustomKtfmtConfig() {
        toggleOffOn()
        trimTrailingWhitespace()
        endWithNewline()
        ktfmt("0.54").googleStyle().configure {
            it.setMaxWidth(120)
            it.setBlockIndent(4)
            it.setContinuationIndent(4)
            it.setRemoveUnusedImports(true)
            it.setManageTrailingCommas(true)
        }
    }

    kotlin {
        target("src/*/kotlin/**/*.kt")
        leadingSpacesToTabs()
        applyCustomKtfmtConfig()
    }

    kotlinGradle {
        target("*.gradle.kts")
        applyCustomKtfmtConfig()
    }
}
