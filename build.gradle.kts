plugins {
    id("online.keriils.plugins.spotless-wrapper") version "0.1.4"
    //    id("online.keriils.plugins.dependency-declaration-convention") version "0.1.4"
}

allprojects {
    apply { plugin("online.keriils.plugins.spotless-wrapper") }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.extra["configurePom"] =
    fun MavenPom.(archiveName: String) {
        name = archiveName
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
