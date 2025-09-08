package online.keriils.gradle.dsl.extension

import cn.elytra.gradle.conventions.objects.ModpackVersion
import online.keriils.gradle.dsl.api.DependencyDeclarationHandler
import online.keriils.gradle.utils.ConstantsHelper
import online.keriils.gradle.utils.ElytraModpackVersionAccessor
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import org.jetbrains.kotlin.gradle.plugin.extraProperties

/// The decorator context does not provide an object factory
public abstract class DependencyDeclarationsExtension(project: Project) : ExtensionAware {

    public val handlers: ListProperty<DependencyDeclarationHandler> =
        project.objects.listProperty(DependencyDeclarationHandler::class.java)

    public val gtnhVersion: Property<String> =
        project.objects.property<String>().convention(ConstantsHelper.NO_SET).apply { finalizeValueOnRead() }

    internal val elytraModpackVersion: ModpackVersion by lazy {
        val ver = gtnhVersion.get()
        if (ver == ConstantsHelper.NO_SET) {
            project.extraProperties[ConstantsHelper.ELYTRA_MODPACK_VERSION_KEY] as? ModpackVersion
                ?: throw IllegalStateException(ConstantsHelper.THIS_A_BUG)
        } else {
            ElytraModpackVersionAccessor.newModpackVersion(project, ver)
        }
    }
}
