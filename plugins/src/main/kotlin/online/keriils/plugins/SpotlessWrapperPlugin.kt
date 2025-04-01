package online.keriils.plugins

import com.diffplug.gradle.spotless.BaseKotlinExtension
import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import org.gradle.api.Plugin
import org.gradle.api.Project

open class SpotlessWrapperExtension {
    var enforceSpotlessCheck = false
    var spotlessConfigJava = true
    var spotlessConfigKotlin = true
    var spotlessConfigGroovyGradle = true
    var spotlessConfigKotlinGradle = true
}

@Suppress("unused")
open class SpotlessWrapperPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.applySpotlessConfig(project.extensions.create("wrapperSpotless", SpotlessWrapperExtension::class.java))
    }
}

fun Project.applySpotlessConfig(wrapperSpotless: SpotlessWrapperExtension) {

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

    fun extractDefaultConfigFileFromPluginJar(pluginClass: Class<*>): File {
        val defaultConfigResource =
            pluginClass.getResourceAsStream("/spotless/eclipseformat.xml")
                ?: throw RuntimeException("Default Spotless configuration file not found in resources.")

        val tempFile = File.createTempFile("spotlessWrapper.plugin", ".xml")
        tempFile.deleteOnExit()

        defaultConfigResource.use { input -> Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING) }

        return tempFile
    }

    plugins.apply("com.diffplug.spotless")

    project.afterEvaluate {
        project.extensions.configure<SpotlessExtension>("spotless") {
            encoding("UTF-8")

            if (!wrapperSpotless.enforceSpotlessCheck) isEnforceCheck = false

            format("misc") {
                target(".gitignore")
                leadingSpacesToTabs()
                applyCommonFormatSteps()
            }

            if (wrapperSpotless.spotlessConfigJava) {
                java {
                    target("src/*/java/**/*.java", "src/*/scala/**/*.java")

                    formatAnnotations()
                    removeUnusedImports()
                    applyCommonFormatSteps()
                    importOrder("java", "javax", "net", "org", "com")
                    eclipse("4.19").configFile(extractDefaultConfigFileFromPluginJar(this.javaClass))
                }
            }

            if (wrapperSpotless.spotlessConfigKotlin) {
                kotlin {
                    target("src/*/kotlin/**/*.kt")
                    leadingSpacesToTabs()
                    applyCustomKtfmtConfig()
                    applyCommonFormatSteps()
                }
            }

            if (wrapperSpotless.spotlessConfigKotlinGradle) {
                kotlinGradle {
                    target("*.gradle.kts")
                    applyCustomKtfmtConfig()
                    applyCommonFormatSteps()
                }
            }

            if (wrapperSpotless.spotlessConfigGroovyGradle) {
                groovyGradle {
                    target("*.gradle")

                    removeSemicolons()
                    applyCommonFormatSteps()
                    importOrder("java", "javax", "net", "org", "com")
                }
            }
        }
    }
}
