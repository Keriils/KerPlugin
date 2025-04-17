import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `kotlin-dsl`
    signing
    id("com.vanniktech.maven.publish") version "0.31.0"
}

version = "0.1.3"

group = "online.keriils.plugins"

val archiveName = "KerGradlePlugin"

tasks.jar { archiveBaseName.set(archiveName) }

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

dependencies { implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2") }

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
    configure(GradlePlugin(javadocJar = JavadocJar.Javadoc(), sourcesJar = true))
    coordinates(group as String, archiveName, version as String)

    pom {
        @Suppress("UNCHECKED_CAST")
        (rootProject.extra["configurePom"] as Function2<MavenPom, String, Unit>)(this, archiveName)
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
