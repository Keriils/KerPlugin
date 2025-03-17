import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `kotlin-dsl`
    signing
    id("com.vanniktech.maven.publish") version "0.31.0"
}

group = "online.keriils.plugins"

version = "0.1.0"

kotlin { jvmToolchain(21) }

repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

dependencies { implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2") }

java {
    withSourcesJar()
    withJavadocJar()
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

gradlePlugin {
    plugins {
        create("spotlessWrapper") {
            id = "$group.spotless-wrapper"
            implementationClass = "$group.SpotlessWrapperPlugin"
            displayName = "SpotlessWrapper"
            description = "Wrapping my usual spotless plugin settings."
        }
    }
}

mavenPublishing {
    coordinates(group as String, rootProject.name, version as String)

    pom {
        name = rootProject.name
        url = "https://github.com/Keriils/KerPlugin"
        description = "My custom gradle plugin for various generic projects, w...."

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/mit"
                distribution = "https://opensource.org/license/mit"
            }
        }

        developers {
            developer {
                name = "Keriils"
                email = "keriils725@126.com"
                url = "https://github.com/Keriils"
            }
        }

        scm {
            connection.set("scm:git:git://github.com/Keriils/KerPlugin.git")
            developerConnection.set("scm:git:ssh://github.com/Keriils/KerPlugin.git")
            url.set("https://github.com/Keriils/KerPlugin")
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

