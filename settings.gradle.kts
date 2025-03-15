rootProject.name = "KerPlugin"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0" }
