package online.keriils.gradle.dsl.api

import online.keriils.gradle.utils.ConstantsHelper
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ExternalModuleDependency

public interface DependencyDeclarationHandler : ConfigurationWrapper {

    public fun getDependencyVersion(name: String): String

    public fun declare(name: String): Dependency? = declare(name, ConstantsHelper.NOTATION_GTNH_CLASSIFIER)

    public fun declare(name: String, classifier: String): Dependency?

    public fun declare(
        name: String,
        dependencyConfiguration: Action<ExternalModuleDependency>,
    ): ExternalModuleDependency = declare(name, ConstantsHelper.NOTATION_GTNH_CLASSIFIER, dependencyConfiguration)

    public fun declare(
        name: String,
        classifier: String,
        dependencyConfiguration: Action<ExternalModuleDependency>,
    ): ExternalModuleDependency

    public fun declareConstraint(name: String): DependencyConstraint =
        declareConstraint(name, ConstantsHelper.NOTATION_GTNH_CLASSIFIER)

    public fun declareConstraint(name: String, classifier: String): DependencyConstraint

    public fun declareConstraint(name: String, block: DependencyConstraint.() -> Unit): DependencyConstraint =
        declareConstraint(name, ConstantsHelper.NOTATION_GTNH_CLASSIFIER, block)

    public fun declareConstraint(
        name: String,
        classifier: String,
        block: DependencyConstraint.() -> Unit,
    ): DependencyConstraint
}
