package online.keriils.gradle.plugins

import com.diffplug.gradle.spotless.SpotlessPlugin
import online.keriils.gradle.dsl.extension.SpotlessWrapperExtension
import online.keriils.gradle.internal.spotless.DslSetting
import online.keriils.gradle.internal.spotless.IdeaIntegration
import org.gradle.api.Plugin
import org.gradle.api.Project

public abstract class SpotlessWrapperPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        /// apply spotless gradle plugin
        project.pluginManager.apply(SpotlessPlugin::class.java)

        /// create our wrapper dsl obj
        project.extensions.create("wrapperSpotless", SpotlessWrapperExtension::class.java)

        /// apply the settings of our wrapped Dsl
        DslSetting.apply(project)

        /// integration with ide
        IdeaIntegration.apply(project)
    }
}
