package online.keriils.gradle.dsl.extension

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

public open class SpotlessWrapperExtension @Inject constructor(objects: ObjectFactory) {

    public val alwaysApplySpotless: Property<Boolean> = objects.property<Boolean>().convention(false)

    public val enforceSpotlessCheck: Property<Boolean> = objects.property<Boolean>().convention(false)

    public val enforceSpotlessCheckForCIEnv: Property<Boolean> = objects.property<Boolean>().convention(false)

    public val spotlessForJava: Property<Boolean> = objects.property<Boolean>().convention(true)

    public val spotlessForKotlin: Property<Boolean> = objects.property<Boolean>().convention(true)

    public val spotlessForGroovyGradle: Property<Boolean> = objects.property<Boolean>().convention(true)

    public val spotlessForKotlinGradle: Property<Boolean> = objects.property<Boolean>().convention(true)

    public val spotlessForMiscFile: ListProperty<String> =
        objects.listProperty(String::class.java).convention(mutableListOf(".gitignore")).apply { finalizeValueOnRead() }
}
