package online.keriils.gradle.internal.spotless

import online.keriils.gradle.utils.ConstantsHelper
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.IdeaExtPlugin
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.RunConfigurationContainer

internal object IdeaIntegration {

    internal fun apply(project: Project) {
        /// only applied for root project
        if (project != project.rootProject) return

        project.pluginManager.apply(IdeaExtPlugin::class.java)
        val ideaModel = project.extensions.getByType(IdeaModel::class.java)
        val ideaProject = ideaModel.project ?: throw IllegalStateException(ConstantsHelper.THIS_A_BUG)
        val ideaExt = (ideaProject as ExtensionAware).extensions.getByType(ProjectSettings::class.java)
        val runCfgContainer = (ideaExt as ExtensionAware).extensions.getByType(RunConfigurationContainer::class.java)
        runCfgContainer.register("Apply Spotless", Gradle::class.java) { taskNames = listOf("spotlessApply") }
    }
}
