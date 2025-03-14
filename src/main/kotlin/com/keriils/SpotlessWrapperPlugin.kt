package com.keriils

import com.diffplug.gradle.spotless.BaseKotlinExtension
import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class SpotlessWrapperPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.applySpotlessConfig()
    }
}

val applySpotlessConfig =
    fun Project.() {

        fun FormatExtension.applyCommonFormatSteps() {
            toggleOffOn()
            trimTrailingWhitespace()
            endWithNewline()
        }

        @Suppress("SpellCheckingInspection")
        fun BaseKotlinExtension.applyCustomKtfmtConfig() {
            ktfmt("0.54").googleStyle().configure {
                it.setMaxWidth(120)
                it.setBlockIndent(4)
                it.setContinuationIndent(4)
                it.setRemoveUnusedImports(true)
                it.setManageTrailingCommas(true)
            }
        }

        plugins.apply("com.diffplug.spotless")

        project.extensions.configure<SpotlessExtension>("spotless") {
            encoding("UTF-8")

            format("misc") {
                target(".gitignore")
                leadingSpacesToTabs()
                applyCommonFormatSteps()
            }

            java {
                target("src/*/java/**/*.java", "src/*/scala/**/*.java")

                formatAnnotations()
                removeUnusedImports()
                applyCommonFormatSteps()
                importOrder("java", "javax", "net", "org", "com")
                eclipse("4.19").configFile(file("SpotlessPlugin/spotless.eclipseFormat.xml"))
            }

            kotlin {
                target("src/*/kotlin/**/*.kt")
                leadingSpacesToTabs()
                applyCustomKtfmtConfig()
                applyCommonFormatSteps()
            }

            kotlinGradle {
                target("*.gradle.kts")
                applyCustomKtfmtConfig()
                applyCommonFormatSteps()
            }

            groovyGradle {
                target("*.gradle")

                removeSemicolons()
                applyCommonFormatSteps()
                importOrder("java", "javax", "net", "org", "com")
            }
        }
    }
