package online.keriils.gradle.utils

import cn.elytra.gradle.conventions.Manifest
import cn.elytra.gradle.conventions.objects.ModpackVersion
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import org.gradle.api.Project

public object ElytraModpackVersionAccessor {

    private var instance: Any? = null
    private var getManifestMethod: Method? = null
    private var extractManifestToMapMethod: Method? = null
    private var modpackVersionConstructor: Constructor<out Any>? = null

    public fun newModpackVersion(
        project: Project,
        manifestVersion: String,
        allowFromCaching: Boolean = true,
    ): ModpackVersion {
        if (instance == null) loadMethod()
        val manifest = getManifestMethod?.invoke(instance, project, manifestVersion, allowFromCaching)
        val map = extractManifestToMapMethod?.invoke(instance, manifest)
        val newInstance =
            modpackVersionConstructor?.newInstance(map) as? ModpackVersion ?: throw IllegalArgumentException()
        return newInstance
    }

    private fun loadMethod() {
        val mfClazz = Class.forName("cn.elytra.gradle.conventions.internal.ManifestUtils")
        instance = mfClazz.getDeclaredField("INSTANCE").get(null)
        getManifestMethod =
            mfClazz.getDeclaredMethod(
                "getManifest\$plugin",
                Project::class.java,
                String::class.java,
                Boolean::class.java,
            )
        extractManifestToMapMethod = mfClazz.getDeclaredMethod("extractManifestToMap\$plugin", Manifest::class.java)
        getManifestMethod?.setAccessible(true)
        extractManifestToMapMethod?.setAccessible(true)

        val mvClazz = Class.forName("cn.elytra.gradle.conventions.objects.ModpackVersion")
        modpackVersionConstructor = mvClazz.getDeclaredConstructor(Map::class.java)
        modpackVersionConstructor?.setAccessible(true)
    }
}
