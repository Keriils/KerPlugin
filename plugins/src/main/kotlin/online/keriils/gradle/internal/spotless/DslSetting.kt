package online.keriils.gradle.internal.spotless

import com.diffplug.gradle.spotless.BaseKotlinExtension
import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import java.io.File
import kotlin.reflect.KClass
import online.keriils.gradle.dsl.extension.SpotlessWrapperExtension
import online.keriils.gradle.utils.ConstantsHelper
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal object DslSetting {

    internal fun apply(project: Project) {

        project.afterEvaluate {
            val spotlessExtension = findExt(SpotlessExtension::class)
            val wrapperExtension = findExt(SpotlessWrapperExtension::class)

            spotlessExtension.encoding("UTF-8")

            val spotlessCheckTask = project.tasks.named(ConstantsHelper.TASK_SPOTLESS_CHECK)
            val checkTask = project.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).get()
            wrapperExtension.enforceSpotlessCheck.get().apply {
                spotlessExtension.isEnforceCheck = this
                if (this) {
                    checkTask.dependsOn(spotlessCheckTask)
                } else {
                    checkTask.setDependsOn(checkTask.dependsOn.filter { it != spotlessCheckTask.get() })
                }
            }

            /// used in ci env for spotless check
            if (wrapperExtension.enforceSpotlessCheckForCIEnv.get()) {
                val isCiEnvironment = System.getenv("CI") == "true" || System.getenv("GITHUB_ACTIONS") == "true"
                /// debug optional
                val simulateEnvValue = project.properties[ConstantsHelper.SPOTLESS_CI_DEBUG_PROPERTY] == "true"
                if (isCiEnvironment || simulateEnvValue) {
                    project.tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME) {
                        dependsOn(ConstantsHelper.TASK_SPOTLESS_CHECK)
                    }
                }
            }

            val alwaysApplySpotless = project.properties[ConstantsHelper.ALWAYS_APPY_SPOTLESS] == "true"
            if (wrapperExtension.alwaysApplySpotless.get() || alwaysApplySpotless) {
                val taskProvider = project.tasks.named(ConstantsHelper.TASK_SPOTLESS_APPLY)
                project.tasks.withType(JavaCompile::class.java) { dependsOn(taskProvider) }
            }

            /// formatting for other misc items
            spotlessExtension.format("misc") {
                target(*wrapperExtension.spotlessForMiscFile.get().toTypedArray())

                leadingSpacesToTabs()
                applyCommonFormatSteps()
            }

            /// formatting for Java code
            if (wrapperExtension.spotlessForJava.get()) {
                spotlessExtension.java {
                    target("src/*/java/**/*.java", "src/*/scala/**/*.java")

                    formatAnnotations()
                    removeUnusedImports()
                    applyCommonFormatSteps()
                    importOrder("java", "javax", "net", "org", "com")
                    eclipse("4.19").configFile(extractDefaultConfigFileFromPluginJar(project))
                }
            }

            /// formatting for Kotlin code
            if (wrapperExtension.spotlessForKotlin.get()) {
                spotlessExtension.kotlin {
                    target("src/*/kotlin/**/*.kt")

                    leadingSpacesToTabs()
                    applyCustomKtfmtConfig()
                    applyCommonFormatSteps()
                }
            }

            /// formatting for Kotlin Dsl code
            if (wrapperExtension.spotlessForKotlinGradle.get()) {
                spotlessExtension.kotlinGradle {
                    target("*.gradle.kts")

                    applyCustomKtfmtConfig()
                    applyCommonFormatSteps()
                }
            }

            /// formatting for Kotlin Dsl code
            if (wrapperExtension.spotlessForGroovyGradle.get()) {
                spotlessExtension.groovyGradle {
                    target("*.gradle")

                    removeSemicolons()
                    applyCommonFormatSteps()
                    importOrder("java", "javax", "net", "org", "com")
                }
            }
        }
    }

    private fun <T : Any> Project.findExt(type: KClass<T>): T {
        return this.extensions.findByType(type.java) ?: throw IllegalStateException(ConstantsHelper.THIS_A_BUG)
    }

    private fun FormatExtension.applyCommonFormatSteps() {
        toggleOffOn()
        trimTrailingWhitespace()
        endWithNewline()
    }

    private fun BaseKotlinExtension.applyCustomKtfmtConfig() {
        ktfmt("0.54").googleStyle().configure {
            it.setMaxWidth(120)
            it.setBlockIndent(4)
            it.setContinuationIndent(4)
            it.setRemoveUnusedImports(true)
            it.setManageTrailingCommas(true)
        }
    }

    private fun extractDefaultConfigFileFromPluginJar(project: Project): File {
        val cacheFile = project.layout.buildDirectory.dir("kerPlugin/spotless").get().file("eclipseformat.xml").asFile
        cacheFile.mkdirs()
        if (cacheFile.exists()) cacheFile.delete()

        val resourceStream =
            this.javaClass.getResourceAsStream("/spotless/eclipseformat.xml")
                ?: throw RuntimeException("Default Spotless configuration file not found in resources.")

        cacheFile.outputStream().use { output -> resourceStream.use { input -> input.copyTo(output) } }

        return cacheFile
    }
}
