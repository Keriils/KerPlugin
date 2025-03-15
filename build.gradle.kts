plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("com.keriils.spotless-wrapper") version "0.2-SNAPSHOT"
}

group = "com.keriils"

version = "0.2-SNAPSHOT"

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
            artifactId = "KerPlugin"
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()

        //        maven {
        //            name = "GithubPackages"
        //            url = uri("https://maven.pkg.github.com/OWNER/REPOSITORY")
        //            credentials {
        //                username = project.findProperty("GitUserName") as String? ?: System.getenv("USERNAME")
        //                password = project.findProperty("GitHub.All") as String? ?: System.getenv("TOKEN")
        //            }
        //        }

    }
}
