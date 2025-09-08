package online.keriils.gradle.plugins

import cn.elytra.gradle.conventions.ElytraConventionsPlugin
import online.keriils.gradle.dsl.api.DependencyDeclarationHandler
import online.keriils.gradle.dsl.extension.DependencyDeclarationsExtension
import online.keriils.gradle.dsl.extension.GtnhDependencyExtension
import online.keriils.gradle.utils.ConstantsHelper
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class GtnhDependencyDeclarePlugin : Plugin<Project> {

    @Suppress("PrivatePropertyName")
    private val DEFAULT_CONFIGURATION =
        listOf(
            // api
            "api",
            "apiCompileOnly",
            "apiImplementation",
            "apiRuntimeOnly",

            // implementation
            "implementation",

            // compile
            "compile",
            "compileOnly",
            "compileOnlyApi",
            "runtimeOnly",
            "runtimeOnlyNonPublishable",
            "devOnlyNonPublishable",

            // test
            "testCompile",
            "testCompileOnly",
            "testImplementation",
            "testRuntimeOnly",
        )

    override fun apply(project: Project) {

        project.pluginManager.apply(ElytraConventionsPlugin::class.java)

        project.dependencies.extensions
            .create("dependencyDeclarations", DependencyDeclarationsExtension::class.java, project)
            .apply {
                getConfigurations(project).forEach { cfg ->
                    handlers.add(
                        extensions.create(
                            DependencyDeclarationHandler::class.java,
                            "declaration${cfg.replaceFirstChar { it.uppercase() }}",
                            GtnhDependencyExtension::class.java,
                            project,
                            cfg,
                            this,
                        )
                    )
                    handlers.finalizeValue()
                }
            }
    }

    private fun getConfigurations(project: Project): List<String> {
        val list =
            (project.properties[ConstantsHelper.SPOTLESS_CI_DEBUG_PROPERTY] as? String)?.split(",")?.map { it.trim() }
                ?: return DEFAULT_CONFIGURATION

        return (DEFAULT_CONFIGURATION + list).distinct()
    }
}
