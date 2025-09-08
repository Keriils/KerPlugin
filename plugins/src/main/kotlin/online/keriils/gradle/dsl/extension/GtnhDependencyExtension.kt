package online.keriils.gradle.dsl.extension

import cn.elytra.gradle.conventions.objects.ModpackVersion
import online.keriils.gradle.dsl.api.DependencyDeclarationHandler
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo

public open class GtnhDependencyExtension(
    private val project: Project,
    private val cfgContainerName: String,
    private val declarationsExtension: DependencyDeclarationsExtension,
) : DependencyDeclarationHandler {

    private val modpackVersion: ModpackVersion by lazy { declarationsExtension.elytraModpackVersion }

    override val configuration: Configuration by lazy { project.configurations.getByName(cfgContainerName) }

    override val wrappedConfiguration: String
        get() = cfgContainerName

    override fun getDependencyVersion(name: String): String =
        modpackVersion[name] ?: throw IllegalArgumentException("Name Is Invalid")

    override fun declare(name: String, classifier: String): Dependency? =
        project.dependencies.add(cfgContainerName, modpackVersion.gtnh(name, classifier))

    override fun declare(
        name: String,
        classifier: String,
        dependencyConfiguration: Action<ExternalModuleDependency>,
    ): ExternalModuleDependency =
        addDependencyTo(
            project.dependencies,
            cfgContainerName,
            modpackVersion.gtnh(name, classifier),
            dependencyConfiguration,
        )

    override fun declareConstraint(name: String, classifier: String): DependencyConstraint =
        project.dependencies.constraints.add(cfgContainerName, modpackVersion.gtnh(name, classifier))

    override fun declareConstraint(
        name: String,
        classifier: String,
        block: DependencyConstraint.() -> Unit,
    ): DependencyConstraint =
        project.dependencies.constraints.add(cfgContainerName, modpackVersion.gtnh(name, classifier), block)
}
