plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
//    id("com.keriils.spotless-wrapper") version "0.1.0"
}

group = "com.keriils"

version = "0.1.0"

repositories {
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
}

dependencies { implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2") }

java {
    withSourcesJar()
    withJavadocJar()
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

kotlin { jvmToolchain(21) }

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

publishing {
    publications {
        create<MavenPublication>("KerPlugin") {
            artifactId = rootProject.name
            groupId = project.group as String
            version = project.version as String
            from(components["java"])
        }
    }

    repositories { mavenLocal() }
}
